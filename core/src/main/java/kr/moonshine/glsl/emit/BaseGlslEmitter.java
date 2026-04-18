package kr.moonshine.glsl.emit;

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

import java.util.stream.Collectors;

abstract class BaseGlslEmitter {

    protected final EmitMode mode;

    protected BaseGlslEmitter(EmitMode mode) {
        this.mode = mode;
    }

    protected String nl() {
        return mode == EmitMode.MINIFIED ? "" : "\n";
    }

    protected String sep() {
        return mode == EmitMode.MINIFIED ? "" : " ";
    }

    protected String indent(String s) {
        if (mode == EmitMode.MINIFIED) return s;
        return s.lines()
                .map(line -> "    " + line)
                .collect(Collectors.joining("\n"));
    }

    protected String emitStatement(Statement node) {
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
            case MacroInvocationStatement s -> emitMacroInvocation(s);
            case IntegerSwitchStatement s -> emitIntegerSwitch(s);
            case LocalVariableDeclarationStatement s -> emitLocalVarDecl(s);
            case VoidFunctionCallStatement s -> emitVoidFunctionCall(s);
        };
    }

    protected String emitIf(IfStatement s) {
        var sb = new StringBuilder();
        sb.append("if (").append(emitExpr(s.condition())).append(") ");
        sb.append(emitBlock(s.thenBlock()));
        if (s.elseBlock() != null) {
            sb.append(sep()).append("else ");
            if (s.elseBlock() instanceof IfStatement elseIf) {
                sb.append(emitIf(elseIf));
            } else {
                sb.append(emitStatement(s.elseBlock()));
            }
        }
        return sb.toString();
    }

    protected String emitFor(ForStatement s) {
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

    protected String emitWhile(WhileStatement s) {
        return "while (" + emitExpr(s.condition()) + ") " + emitBlock(s.body());
    }

    protected String emitBlock(Block node) {
        if (node.statements().isEmpty()) return "{}";
        var inner = node.statements().stream()
                .map(s -> indent(emitStatement(s)))
                .collect(Collectors.joining(nl()));
        return "{" + nl() + inner + nl() + "}";
    }

    protected String emitMacroInvocation(MacroInvocationStatement s) {
        if (s.arguments().isEmpty()) return s.name() + ";";
        var args = s.arguments().stream()
                .map(this::emitExpr)
                .collect(Collectors.joining(", "));
        return s.name() + "(" + args + ");";
    }

    protected String emitVoidFunctionCall(VoidFunctionCallStatement s) {
        if (s.arguments().isEmpty()) return s.name() + "();";
        var args = s.arguments().stream()
                .map(this::emitExpr)
                .collect(Collectors.joining("," + sep()));
        return s.name() + "(" + args + ");";
    }

    protected String emitIntegerSwitch(IntegerSwitchStatement s) {
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

    protected String emitLocalVarDecl(LocalVariableDeclarationStatement s) {
        var sb = new StringBuilder();
        if (s.isConst()) sb.append("const ");
        sb.append(s.glslType().glslName()).append(' ').append(s.name());
        if (s.initializer() != null) {
            sb.append(" = ").append(emitExpr(s.initializer()));
        }
        sb.append(';');
        return sb.toString();
    }

    protected String emitExpr(Expression node) {
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
}
