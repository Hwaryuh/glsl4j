package kr.moonshine.glsl.emit;

import kr.moonshine.glsl.ast.DeclarationWrapper;
import kr.moonshine.glsl.ast.PreprocessorWrapper;
import kr.moonshine.glsl.ast.ShaderUnit;
import kr.moonshine.glsl.ast.TopLevelNode;
import kr.moonshine.glsl.ast.decl.Declaration;
import kr.moonshine.glsl.ast.decl.FunctionDeclaration;
import kr.moonshine.glsl.ast.decl.StorageQualifier;
import kr.moonshine.glsl.ast.decl.StructDeclaration;
import kr.moonshine.glsl.ast.decl.VariableDeclaration;
import kr.moonshine.glsl.ast.expr.ArrayConstructorExpression;
import kr.moonshine.glsl.ast.expr.ArrayIndexExpression;
import kr.moonshine.glsl.ast.expr.BinaryExpression;
import kr.moonshine.glsl.ast.expr.BoolLiteral;
import kr.moonshine.glsl.ast.expr.Builtin;
import kr.moonshine.glsl.ast.expr.Expression;
import kr.moonshine.glsl.ast.expr.FloatLiteral;
import kr.moonshine.glsl.ast.expr.FunctionCallExpression;
import kr.moonshine.glsl.ast.expr.IntLiteral;
import kr.moonshine.glsl.ast.expr.LiteralExpression;
import kr.moonshine.glsl.ast.expr.MacroCallExpression;
import kr.moonshine.glsl.ast.expr.SwizzleExpression;
import kr.moonshine.glsl.ast.expr.TernaryExpression;
import kr.moonshine.glsl.ast.expr.UnaryExpression;
import kr.moonshine.glsl.ast.expr.VariableExpression;
import kr.moonshine.glsl.ast.stmt.AssignmentStatement;
import kr.moonshine.glsl.ast.stmt.Block;
import kr.moonshine.glsl.ast.stmt.BreakStatement;
import kr.moonshine.glsl.ast.stmt.ContinueStatement;
import kr.moonshine.glsl.ast.stmt.DiscardStatement;
import kr.moonshine.glsl.ast.stmt.ExpressionStatement;
import kr.moonshine.glsl.ast.stmt.ForStatement;
import kr.moonshine.glsl.ast.stmt.IfStatement;
import kr.moonshine.glsl.ast.stmt.IntegerSwitchStatement;
import kr.moonshine.glsl.ast.stmt.LocalVariableDeclarationStatement;
import kr.moonshine.glsl.ast.stmt.MacroInvocationStatement;
import kr.moonshine.glsl.ast.stmt.ReturnStatement;
import kr.moonshine.glsl.ast.stmt.Statement;
import kr.moonshine.glsl.ast.stmt.VoidFunctionCallStatement;
import kr.moonshine.glsl.ast.stmt.WhileStatement;
import kr.moonshine.glsl.dialect.GlslDialect;
import kr.moonshine.glsl.type.FunctionReturnType;

import java.util.stream.Collectors;

public final class GlslEmitter {

    private final GlslDialect dialect;
    private final EmitMode mode;

    public GlslEmitter(GlslDialect dialect, EmitMode mode) {
        this.dialect = dialect;
        this.mode = mode;
    }

    public String emit(ShaderUnit unit) {
        var sb = new StringBuilder();
        sb.append("#version ").append(unit.version().versionString()).append('\n');
        for (var node : unit.nodes()) {
            sb.append(emitTopLevel(node)).append('\n');
        }
        return sb.toString();
    }

    private String nl() {
        return mode == EmitMode.MINIFIED ? "" : "\n";
    }

    private String emitTopLevel(TopLevelNode node) {
        return switch (node) {
            case PreprocessorWrapper w -> dialect.emitDirective(w.node());
            case DeclarationWrapper w -> emitDeclaration(w.node());
        };
    }

    private String emitDeclaration(Declaration node) {
        return switch (node) {
            case VariableDeclaration d -> emitVariableDecl(d);
            case FunctionDeclaration d -> emitFunctionDecl(d);
            case StructDeclaration d -> emitStructDecl(d);
        };
    }

    private String emitVariableDecl(VariableDeclaration d) {
        var sb = new StringBuilder();
        if (d.qualifier() != StorageQualifier.LOCAL) {
            sb.append(dialect.emitQualifier(d.qualifier())).append(' ');
        }
        sb.append(d.glslType().glslName()).append(' ').append(d.name());
        if (d.initializer() != null) {
            sb.append(" = ").append(emitExpr(d.initializer()));
        }
        sb.append(';');
        return sb.toString();
    }

    private String emitFunctionDecl(FunctionDeclaration d) {
        var sb = new StringBuilder();
        sb.append(emitReturnType(d.returnType())).append(' ').append(d.name()).append('(');
        var params = d.parameters().stream()
                .map(p -> p.glslType().glslName() + ' ' + p.name())
                .collect(Collectors.joining(", "));
        sb.append(params).append(") ");
        sb.append(emitBlock(d.body()));
        return sb.toString();
    }

    private String emitReturnType(FunctionReturnType returnType) {
        return returnType.glslName();
    }

    private String emitStructDecl(StructDeclaration d) {
        var sb = new StringBuilder();
        sb.append("struct ").append(d.name()).append(sep()).append("{").append(nl());
        for (var field : d.fields()) {
            sb.append(indent(field.glslType().glslName() + ' ' + field.name() + ';')).append(nl());
        }
        sb.append("};");
        return sb.toString();
    }

    private String emitStatement(Statement node) {
        return switch (node) {
            case AssignmentStatement s ->
                    emitExpr(s.target()) + sep() + s.operator().symbol() + sep() + emitExpr(s.value()) + ";";
            case ExpressionStatement s -> emitExpr(s.expression()) + ";";
            case ReturnStatement s -> s.value() == null ? "return;" : "return " + emitExpr(s.value()) + ";";
            case DiscardStatement s -> "discard;";
            case BreakStatement s -> "break;";
            case ContinueStatement s -> "continue;";
            case IfStatement s -> emitIf(s);
            case ForStatement s -> emitFor(s);
            case WhileStatement s -> emitWhile(s);
            case Block s -> emitBlock(s);
            case MacroInvocationStatement s -> emitMacroCall(s);
            case IntegerSwitchStatement s -> emitIntegerSwitch(s);
            case LocalVariableDeclarationStatement s -> emitLocalVarDecl(s);
            case VoidFunctionCallStatement s -> emitVoidFunctionCall(s);
        };
    }

    private String emitIf(IfStatement s) {
        var sb = new StringBuilder();
        sb.append("if (").append(emitExpr(s.condition())).append(") ");
        sb.append(emitBlock(s.thenBlock()));
        if (s.elseBlock() != null) {
            sb.append(sep()).append("else ");
            // else if 체인: elseBlock이 IfStatement면 중괄호 없이 바로 이어붙인다
            if (s.elseBlock() instanceof IfStatement elseIf) {
                sb.append(emitIf(elseIf));
            } else {
                sb.append(emitStatement(s.elseBlock()));
            }
        }
        return sb.toString();
    }

    private String emitFor(ForStatement s) {
        var init = s.init() == null ? "" : emitForClause(s.init());
        var condition = s.condition() == null ? "" : emitExpr(s.condition());
        var update = s.update() == null ? "" : emitExpr(s.update());
        return "for (" + init + "; " + condition + "; " + update + ") " + emitBlock(s.body());
    }

    private String emitForClause(Statement s) {
        return switch (s) {
            case LocalVariableDeclarationStatement d -> {
                var sb = new StringBuilder();
                sb.append(d.glslType().glslName()).append(' ').append(d.name());
                if (d.initializer() != null) sb.append(" = ").append(emitExpr(d.initializer()));
                yield sb.toString();
            }
            case AssignmentStatement d ->
                    emitExpr(d.target()) + sep() + d.operator().symbol() + sep() + emitExpr(d.value());
            default -> emitStatement(s);
        };
    }

    private String emitWhile(WhileStatement s) {
        return "while (" + emitExpr(s.condition()) + ") " + emitBlock(s.body());
    }

    private String emitBlock(Block node) {
        if (node.statements().isEmpty()) return "{}";
        var inner = node.statements().stream()
                .map(s -> indent(emitStatement(s)))
                .collect(Collectors.joining(nl()));
        return "{" + nl() + inner + nl() + "}";
    }

    private String emitMacroCall(MacroInvocationStatement s) {
        if (s.arguments().isEmpty()) return s.name() + ";";
        var args = s.arguments().stream()
                .map(this::emitExpr)
                .collect(Collectors.joining(", "));
        return s.name() + "(" + args + ");";
    }

    private String emitVoidFunctionCall(VoidFunctionCallStatement s) {
        if (s.arguments().isEmpty()) return s.name() + "();";
        var args = s.arguments().stream()
                .map(this::emitExpr)
                .collect(Collectors.joining("," + sep()));
        return s.name() + "(" + args + ");";
    }

    private String emitIntegerSwitch(IntegerSwitchStatement s) {
        var sb = new StringBuilder();
        sb.append("switch (").append(emitExpr(s.selector())).append(") {").append(nl());
        for (var c : s.cases()) {
            sb.append(indent("case " + c.id() + ": " + emitBlock(c.body()))).append(nl());
        }
        if (s.defaultCase() != null) {
            sb.append(indent("default: " + emitBlock(s.defaultCase()))).append(nl());
        }
        sb.append("}");
        return sb.toString();
    }

    private String emitLocalVarDecl(LocalVariableDeclarationStatement s) {
        var sb = new StringBuilder();
        if (s.isConst()) sb.append("const ");
        sb.append(s.glslType().glslName()).append(' ').append(s.name());
        if (s.initializer() != null) {
            sb.append(" = ").append(emitExpr(s.initializer()));
        }
        sb.append(';');
        return sb.toString();
    }

    private String emitExpr(Expression node) {
        return switch (node) {
            case LiteralExpression e -> emitLiteral(e);
            case VariableExpression e -> e.name();
            case BinaryExpression e ->
                    "(" + emitExpr(e.left()) + sep() + e.operator().symbol() + sep() + emitExpr(e.right()) + ")";
            case UnaryExpression e -> "(" + e.operator().symbol() + emitExpr(e.operand()) + ")";
            case TernaryExpression e ->
                    "(" + emitExpr(e.condition()) + sep() + "?" + sep() + emitExpr(e.thenExpr()) + sep() + ":" + sep() + emitExpr(e.elseExpr()) + ")";
            case FunctionCallExpression e -> emitFunctionCall(e);
            case SwizzleExpression e -> emitExpr(e.target()) + "." + e.components();
            case Builtin e -> e.name();
            case ArrayConstructorExpression e -> emitArrayConstructor(e);
            case ArrayIndexExpression e -> emitExpr(e.target()) + "[" + emitExpr(e.index()) + "]";
            case MacroCallExpression e -> emitMacroCallExpr(e);
        };
    }

    private String emitArrayConstructor(ArrayConstructorExpression e) {
        var args = e.elements().stream()
                .map(this::emitExpr)
                .collect(Collectors.joining("," + sep()));
        return e.glslType().glslName() + "(" + args + ")";
    }

    private String emitMacroCallExpr(MacroCallExpression e) {
        if (e.arguments().isEmpty()) return e.name() + "()";
        var args = e.arguments().stream()
                .map(this::emitExpr)
                .collect(Collectors.joining("," + sep()));
        return e.name() + "(" + args + ")";
    }

    private String emitFunctionCall(FunctionCallExpression e) {
        var args = e.arguments().stream()
                .map(this::emitExpr)
                .collect(Collectors.joining("," + sep()));
        return e.name() + "(" + args + ")";
    }

    private String emitLiteral(LiteralExpression node) {
        return switch (node) {
            case FloatLiteral e -> {
                String s = Float.toString(e.value());
                yield s.contains(".") ? s : s + ".0";
            }
            case IntLiteral e -> Integer.toString(e.value());
            case BoolLiteral e -> Boolean.toString(e.value());
        };
    }

    private String indent(String s) {
        if (mode == EmitMode.MINIFIED) return s;
        return s.lines()
                .map(line -> "    " + line)
                .collect(Collectors.joining("\n"));
    }

    private String sep() {
        return mode == EmitMode.MINIFIED ? "" : " ";
    }
}
