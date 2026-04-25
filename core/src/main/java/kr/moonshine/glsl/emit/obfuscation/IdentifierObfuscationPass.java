package kr.moonshine.glsl.emit.obfuscation;

import com.google.common.collect.Maps;
import kr.moonshine.glsl.ast.DeclarationWrapper;
import kr.moonshine.glsl.ast.ShaderUnit;
import kr.moonshine.glsl.ast.decl.Declaration;
import kr.moonshine.glsl.ast.decl.FunctionDeclaration;
import kr.moonshine.glsl.ast.decl.FunctionParameter;
import kr.moonshine.glsl.ast.decl.StructDeclaration;
import kr.moonshine.glsl.ast.decl.VariableDeclaration;
import kr.moonshine.glsl.ast.expr.Expression;
import kr.moonshine.glsl.ast.expr.VariableExpression;
import kr.moonshine.glsl.ast.stmt.Block;
import kr.moonshine.glsl.ast.stmt.LocalVariableDeclarationStatement;
import kr.moonshine.glsl.ast.stmt.Statement;
import kr.moonshine.glsl.ast.stmt.VoidFunctionCallStatement;
import kr.moonshine.glsl.emit.ObfuscationFeature;
import kr.moonshine.glsl.emit.ObfuscationPass;
import kr.moonshine.glsl.type.GlslType;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Set;

public final class IdentifierObfuscationPass extends AstRewriter implements ObfuscationPass {

    private static final Set<String> GLSL_RESERVED = Set.of("main",
            "gl_Position", "gl_FragCoord", "gl_FragDepth",
            "gl_PointSize", "gl_VertexID", "gl_InstanceID");

    private final Set<String> externalScope;
    private final Deque<Map<String, String>> scopeStack = new ArrayDeque<>();
    private int counter = 0;

    public IdentifierObfuscationPass() {
        this(Set.of());
    }

    public IdentifierObfuscationPass(Set<String> externalScope) {
        this.externalScope = Set.copyOf(externalScope);
    }

    @Override
    public ShaderUnit apply(ShaderUnit unit, Set<ObfuscationFeature> features) {
        if (!features.contains(ObfuscationFeature.IDENTIFIER)) return unit;
        pushFrame();
        for (var node : unit.nodes()) {
            if (node instanceof DeclarationWrapper(Declaration d)) {
                registerGlobal(d);
            }
        }
        var result = rewriteUnit(unit);
        popFrame();
        return result;
    }

    private void registerGlobal(Declaration d) {
        switch (d) {
            case VariableDeclaration v -> allocate(v.name());
            case FunctionDeclaration f -> allocate(f.name());
            case StructDeclaration ignored -> {
            }
        }
    }

    @Override
    protected FunctionDeclaration rewriteFunctionDeclaration(FunctionDeclaration d) {
        pushFrame();
        var params = d.parameters().stream().map(this::rewriteFunctionParameter).toList();
        var body = rewriteBlock(d.body());
        popFrame();
        return new FunctionDeclaration(d.returnType(), mapped(d.name()), params, body);
    }

    @Override
    protected Block rewriteBlock(Block block) {
        pushFrame();
        var result = super.rewriteBlock(block);
        popFrame();
        return result;
    }

    @Override
    protected VariableDeclaration rewriteVariableDeclaration(VariableDeclaration d) {
        var init = d.initializer() == null ? null : rewriteExpr(d.initializer());
        return new VariableDeclaration(d.qualifier(), d.glslType(), mapped(d.name()), init);
    }

    @Override
    protected FunctionParameter rewriteFunctionParameter(FunctionParameter p) {
        return new FunctionParameter(p.glslType(), allocate(p.name()));
    }

    @Override
    protected Statement rewriteStatement(Statement node) {
        return switch (node) {
            case LocalVariableDeclarationStatement(
                    boolean isConst, GlslType glslType, String name, Expression initializer
            ) -> {
                var init = initializer == null ? null : rewriteExpr(initializer);
                yield new LocalVariableDeclarationStatement(isConst, glslType, allocate(name), init);
            }
            case VoidFunctionCallStatement s -> {
                var args = s.arguments().stream().map(this::rewriteExpr).toList();
                yield new VoidFunctionCallStatement(mapped(s.name()), args);
            }
            default -> super.rewriteStatement(node);
        };
    }

    @Override
    protected VariableExpression rewriteVariableExpression(VariableExpression e) {
        return new VariableExpression(mapped(e.name()), e.glslType());
    }

    private void pushFrame() {
        scopeStack.push(Maps.newHashMap());
    }

    private void popFrame() {
        scopeStack.pop();
    }

    private String allocate(String name) {
        if (isReserved(name)) return name;
        String generated = generateName(counter++);
        scopeStack.peek().put(name, generated);
        return generated;
    }

    private String mapped(String name) {
        for (var frame : scopeStack) {
            if (frame.containsKey(name)) return frame.get(name);
        }
        return name;
    }

    private boolean isReserved(String name) {
        return GLSL_RESERVED.contains(name) || externalScope.contains(name);
    }

    private static String generateName(int index) {
        var sb = new StringBuilder();
        do {
            sb.insert(0, (char) ('a' + index % 26));
            index = index / 26 - 1;
        } while (index >= 0);
        return sb.toString();
    }
}
