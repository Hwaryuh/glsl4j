package kr.moonshine.glsl.validate;

import com.google.common.collect.Maps;
import kr.moonshine.glsl.ShaderStage;
import kr.moonshine.glsl.ast.DeclarationWrapper;
import kr.moonshine.glsl.ast.PreprocessorWrapper;
import kr.moonshine.glsl.ast.ShaderUnit;
import kr.moonshine.glsl.ast.decl.Declaration;
import kr.moonshine.glsl.ast.decl.FunctionDeclaration;
import kr.moonshine.glsl.ast.decl.StorageQualifier;
import kr.moonshine.glsl.ast.decl.StructDeclaration;
import kr.moonshine.glsl.ast.decl.VariableDeclaration;
import kr.moonshine.glsl.ast.expr.ArrayConstructorExpression;
import kr.moonshine.glsl.ast.expr.ArrayIndexExpression;
import kr.moonshine.glsl.ast.expr.Builtin;
import kr.moonshine.glsl.ast.expr.BinaryExpression;
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
import kr.moonshine.glsl.ast.stmt.VoidFunctionCallStatement;
import kr.moonshine.glsl.ast.stmt.WhileStatement;
import kr.moonshine.glsl.type.ScalarType;

import java.util.Map;
import java.util.Set;

public final class GlslValidator {

    private final ShaderStage stage;
    private final Set<String> externalScope;

    public GlslValidator(ShaderStage stage) {
        this(stage, Set.of());
    }

    public GlslValidator(ShaderStage stage, Set<String> externalScope) {
        this.stage = stage;
        this.externalScope = Set.copyOf(externalScope);
    }

    public void validate(ShaderUnit unit) {
        var scope = new Scope(externalScope);
        for (var node : unit.nodes()) {
            switch (node) {
                case DeclarationWrapper w -> validateDeclaration(w.node(), scope);
                case PreprocessorWrapper ignored -> {
                }
            }
        }
    }

    public void validateBlock(Block block) {
        validateBlock(block, new Scope(externalScope));
    }

    private void validateDeclaration(Declaration node, Scope scope) {
        switch (node) {
            case VariableDeclaration d -> {
                if (d.initializer() != null) validateExpr(d.initializer(), scope);
                scope.declare(d.name(), d.qualifier() == StorageQualifier.CONST);
            }
            case FunctionDeclaration d -> {
                var funcScope = new Scope(scope);
                d.parameters().forEach(p -> funcScope.declare(p.name(), false));
                validateBlock(d.body(), funcScope);
            }
            case StructDeclaration ignored -> {
            }
        }
    }

    private void validateBlock(Block block, Scope scope) {
        var blockScope = new Scope(scope);
        for (var stmt : block.statements()) {
            validateStatement(stmt, blockScope);
        }
    }

    private void validateStatement(Statement node, Scope scope) {
        switch (node) {
            case AssignmentStatement s -> {
                validateExpr(s.target(), scope);
                validateExpr(s.value(), scope);
                if (s.target() instanceof Builtin b) {
                    BuiltinValidator.validateWrite(b, stage);
                } else if (s.target() instanceof VariableExpression v) {
                    if (scope.isConst(v.name())) {
                        throw new GlslValidationException("Cannot assign to const variable: " + v.name());
                    }
                }
            }
            case LocalVariableDeclarationStatement s -> {
                if (s.initializer() != null) validateExpr(s.initializer(), scope);
                scope.declare(s.name(), s.isConst());
            }
            case ExpressionStatement s -> validateExpr(s.expression(), scope);
            case ReturnStatement s -> {
                if (s.value() != null) validateExpr(s.value(), scope);
            }
            case DiscardStatement ignored -> {
                if (stage != ShaderStage.FRAGMENT) {
                    throw new GlslValidationException("discard is only allowed in fragment shader");
                }
            }
            case IfStatement s -> {
                validateExpr(s.condition(), scope);
                validateBlock(s.thenBlock(), scope);
                if (s.elseBlock() != null) validateStatement(s.elseBlock(), scope);
            }
            case ForStatement s -> {
                var forScope = new Scope(scope);
                if (s.init() != null) validateStatement(s.init(), forScope);
                if (s.condition() != null) validateExpr(s.condition(), forScope);
                if (s.update() != null) validateExpr(s.update(), forScope);
                validateBlock(s.body(), forScope);
            }
            case WhileStatement s -> {
                validateExpr(s.condition(), scope);
                validateBlock(s.body(), scope);
            }
            case IntegerSwitchStatement s -> {
                validateExpr(s.selector(), scope);
                if (s.selector().glslType() != ScalarType.INT) {
                    throw new GlslValidationException("switch selector must be int");
                }
                s.cases().forEach(c -> validateBlock(c.body(), scope));
                if (s.defaultCase() != null) validateBlock(s.defaultCase(), scope);
            }
            case Block s -> validateBlock(s, scope);
            case BreakStatement ignored -> {
            }
            case ContinueStatement ignored -> {
            }
            case MacroInvocationStatement ignored -> {
            }
            case VoidFunctionCallStatement ignored -> {
            }
        }
    }

    private void validateExpr(Expression node, Scope scope) {
        switch (node) {
            case VariableExpression e -> {
                if (!scope.contains(e.name())) {
                    throw new GlslValidationException("Undeclared variable: " + e.name());
                }
            }
            case Builtin e -> BuiltinValidator.validateRead(e, stage);
            case BinaryExpression e -> {
                validateExpr(e.left(), scope);
                validateExpr(e.right(), scope);
            }
            case UnaryExpression e -> validateExpr(e.operand(), scope);
            case TernaryExpression e -> {
                validateExpr(e.condition(), scope);
                validateExpr(e.thenExpr(), scope);
                validateExpr(e.elseExpr(), scope);
            }
            case FunctionCallExpression e -> e.arguments().forEach(arg -> validateExpr(arg, scope));
            case SwizzleExpression e -> validateExpr(e.target(), scope);
            case ArrayConstructorExpression e -> e.elements().forEach(elem -> validateExpr(elem, scope));
            case ArrayIndexExpression e -> {
                validateExpr(e.target(), scope);
                validateExpr(e.index(), scope);
            }
            case MacroCallExpression e -> e.arguments().forEach(arg -> validateExpr(arg, scope));
            case LiteralExpression ignored -> {
            }
        }
    }

    private static final class Scope {

        private final Scope parent;
        private final Map<String, Boolean> names; // true = const

        Scope(Set<String> externalScope) {
            this.parent = null;
            this.names = Maps.newHashMap();
            externalScope.forEach(name -> names.put(name, false));
        }

        Scope(Scope parent) {
            this.parent = parent;
            this.names = Maps.newHashMap();
        }

        void declare(String name, boolean isConst) {
            names.put(name, isConst);
        }

        boolean contains(String name) {
            if (names.containsKey(name)) return true;
            return parent != null && parent.contains(name);
        }

        boolean isConst(String name) {
            if (names.containsKey(name)) return names.get(name);
            return parent != null && parent.isConst(name);
        }
    }
}
