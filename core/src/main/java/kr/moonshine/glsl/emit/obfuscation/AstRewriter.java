package kr.moonshine.glsl.emit.obfuscation;

import kr.moonshine.glsl.ast.DeclarationWrapper;
import kr.moonshine.glsl.ast.PreprocessorWrapper;
import kr.moonshine.glsl.ast.ShaderUnit;
import kr.moonshine.glsl.ast.TopLevelNode;
import kr.moonshine.glsl.ast.decl.Declaration;
import kr.moonshine.glsl.ast.decl.FunctionDeclaration;
import kr.moonshine.glsl.ast.decl.FunctionParameter;
import kr.moonshine.glsl.ast.decl.StructDeclaration;
import kr.moonshine.glsl.ast.decl.VariableDeclaration;
import kr.moonshine.glsl.ast.expr.ArrayConstructorExpression;
import kr.moonshine.glsl.ast.expr.ArrayIndexExpression;
import kr.moonshine.glsl.ast.expr.BinaryExpression;
import kr.moonshine.glsl.ast.expr.Builtin;
import kr.moonshine.glsl.ast.expr.Expression;
import kr.moonshine.glsl.ast.expr.FunctionCallExpression;
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
import kr.moonshine.glsl.ast.stmt.SwitchCase;
import kr.moonshine.glsl.ast.stmt.VoidFunctionCallStatement;
import kr.moonshine.glsl.ast.stmt.WhileStatement;

public abstract class AstRewriter {

    public ShaderUnit rewriteUnit(ShaderUnit unit) {
        var nodes = unit.nodes().stream()
                .map(this::rewriteTopLevel)
                .toList();
        return new ShaderUnit(unit.name(), unit.extension(), unit.version(), unit.stage(), nodes);
    }

    protected TopLevelNode rewriteTopLevel(TopLevelNode node) {
        return switch (node) {
            case PreprocessorWrapper w -> w;
            case DeclarationWrapper w -> new DeclarationWrapper(rewriteDeclaration(w.node()));
        };
    }

    protected Declaration rewriteDeclaration(Declaration node) {
        return switch (node) {
            case VariableDeclaration d -> rewriteVariableDeclaration(d);
            case FunctionDeclaration d -> rewriteFunctionDeclaration(d);
            case StructDeclaration d -> rewriteStructDeclaration(d);
        };
    }

    protected VariableDeclaration rewriteVariableDeclaration(VariableDeclaration d) {
        var initializer = d.initializer() == null ? null : rewriteExpr(d.initializer());
        return new VariableDeclaration(d.qualifier(), d.glslType(), d.name(), initializer);
    }

    protected FunctionDeclaration rewriteFunctionDeclaration(FunctionDeclaration d) {
        var params = d.parameters().stream().map(this::rewriteFunctionParameter).toList();
        return new FunctionDeclaration(d.returnType(), d.name(), params, rewriteBlock(d.body()));
    }

    protected FunctionParameter rewriteFunctionParameter(FunctionParameter p) {
        return p;
    }

    protected StructDeclaration rewriteStructDeclaration(StructDeclaration d) {
        return d;
    }

    protected Block rewriteBlock(Block block) {
        var stmts = block.statements().stream().map(this::rewriteStatement).toList();
        return new Block(stmts);
    }

    protected Statement rewriteStatement(Statement node) {
        return switch (node) {
            case AssignmentStatement s ->
                    new AssignmentStatement(rewriteExpr(s.target()), s.operator(), rewriteExpr(s.value()));
            case LocalVariableDeclarationStatement s -> {
                var init = s.initializer() == null ? null : rewriteExpr(s.initializer());
                yield new LocalVariableDeclarationStatement(s.isConst(), s.glslType(), s.name(), init);
            }
            case ExpressionStatement s -> new ExpressionStatement(rewriteExpr(s.expression()));
            case ReturnStatement s -> {
                var value = s.value() == null ? null : rewriteExpr(s.value());
                yield new ReturnStatement(value);
            }
            case IfStatement s -> {
                var elseBlock = s.elseBlock() == null ? null : rewriteStatement(s.elseBlock());
                yield new IfStatement(rewriteExpr(s.condition()), rewriteBlock(s.thenBlock()), elseBlock);
            }
            case ForStatement s -> {
                var init = s.init() == null ? null : rewriteStatement(s.init());
                var condition = s.condition() == null ? null : rewriteExpr(s.condition());
                var update = s.update() == null ? null : rewriteExpr(s.update());
                yield new ForStatement(init, condition, update, rewriteBlock(s.body()));
            }
            case WhileStatement s -> new WhileStatement(rewriteExpr(s.condition()), rewriteBlock(s.body()));
            case IntegerSwitchStatement s -> {
                var cases = s.cases().stream().map(this::rewriteSwitchCase).toList();
                var defaultCase = s.defaultCase() == null ? null : rewriteBlock(s.defaultCase());
                yield new IntegerSwitchStatement(rewriteExpr(s.selector()), cases, defaultCase);
            }
            case VoidFunctionCallStatement s -> {
                var args = s.arguments().stream().map(this::rewriteExpr).toList();
                yield new VoidFunctionCallStatement(s.name(), args);
            }
            case Block s -> rewriteBlock(s);
            case BreakStatement s -> s;
            case ContinueStatement s -> s;
            case DiscardStatement s -> s;
            case MacroInvocationStatement s -> {
                var args = s.arguments().stream().map(this::rewriteExpr).toList();
                yield new MacroInvocationStatement(s.name(), args);
            }
        };
    }

    protected SwitchCase rewriteSwitchCase(SwitchCase c) {
        return new SwitchCase(c.id(), rewriteBlock(c.body()));
    }

    protected Expression rewriteExpr(Expression node) {
        return switch (node) {
            case LiteralExpression e -> rewriteLiteral(e);
            case VariableExpression e -> rewriteVariableExpression(e);
            case BinaryExpression e ->
                    new BinaryExpression(rewriteExpr(e.left()), e.operator(), rewriteExpr(e.right()), e.glslType());
            case UnaryExpression e -> new UnaryExpression(e.operator(), rewriteExpr(e.operand()), e.glslType());
            case TernaryExpression e -> new TernaryExpression(
                    rewriteExpr(e.condition()), rewriteExpr(e.thenExpr()), rewriteExpr(e.elseExpr()), e.glslType());
            case FunctionCallExpression e -> {
                var args = e.arguments().stream().map(this::rewriteExpr).toList();
                yield new FunctionCallExpression(e.name(), args, e.glslType());
            }
            case SwizzleExpression e -> new SwizzleExpression(rewriteExpr(e.target()), e.components(), e.glslType());
            case Builtin e -> e;
            case ArrayConstructorExpression e -> {
                var elems = e.elements().stream().map(this::rewriteExpr).toList();
                yield new ArrayConstructorExpression(e.glslType(), elems);
            }
            case ArrayIndexExpression e ->
                    new ArrayIndexExpression(rewriteExpr(e.target()), rewriteExpr(e.index()), e.glslType());
            case MacroCallExpression e -> {
                var args = e.arguments().stream().map(this::rewriteExpr).toList();
                yield new MacroCallExpression(e.name(), args, e.glslType());
            }
        };
    }

    protected Expression rewriteLiteral(LiteralExpression e) {
        return e;
    }

    protected VariableExpression rewriteVariableExpression(VariableExpression e) {
        return e;
    }
}
