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
import kr.moonshine.glsl.ast.stmt.ForStatement;
import kr.moonshine.glsl.ast.stmt.IfStatement;
import kr.moonshine.glsl.ast.stmt.IntegerSwitchStatement;
import kr.moonshine.glsl.ast.stmt.LocalVariableDeclarationStatement;
import kr.moonshine.glsl.ast.stmt.Statement;
import kr.moonshine.glsl.ast.stmt.VoidFunctionCallStatement;
import kr.moonshine.glsl.ast.stmt.WhileStatement;
import kr.moonshine.glsl.emit.ObfuscationFeature;
import kr.moonshine.glsl.emit.ObfuscationPass;
import kr.moonshine.glsl.type.GlslType;

import java.util.Map;
import java.util.Set;

public final class IdentifierObfuscationPass extends AstRewriter implements ObfuscationPass {

    private static final Set<String> GLSL_RESERVED = Set.of("main",
            "gl_Position", "gl_FragCoord", "gl_FragDepth",
            "gl_PointSize", "gl_VertexID", "gl_InstanceID");

    private final Set<String> externalScope;
    private final Map<String, String> mapping = Maps.newHashMap();
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
        collectDeclarations(unit);
        return rewriteUnit(unit);
    }

    private void collectDeclarations(ShaderUnit unit) {
        for (var node : unit.nodes()) {
            if (node instanceof DeclarationWrapper(Declaration w)) {
                collectDeclaration(w);
            }
        }
    }

    private void collectDeclaration(Declaration node) {
        switch (node) {
            case VariableDeclaration d -> allocate(d.name());
            case FunctionDeclaration d -> {
                allocate(d.name());
                d.parameters().forEach(p -> allocate(p.name()));
                collectBlock(d.body());
            }
            case StructDeclaration ignored -> {
            }
        }
    }

    private void collectBlock(Block block) {
        block.statements().forEach(this::collectStatement);
    }

    private void collectStatement(Statement node) {
        switch (node) {
            case LocalVariableDeclarationStatement s -> allocate(s.name());
            case IfStatement s -> {
                collectBlock(s.thenBlock());
                if (s.elseBlock() != null) collectStatement(s.elseBlock());
            }
            case ForStatement s -> {
                if (s.init() != null) collectStatement(s.init());
                collectBlock(s.body());
            }
            case WhileStatement s -> collectBlock(s.body());
            case IntegerSwitchStatement s -> {
                s.cases().forEach(c -> collectBlock(c.body()));
                if (s.defaultCase() != null) collectBlock(s.defaultCase());
            }
            case Block s -> collectBlock(s);
            default -> {
            }
        }
    }

    private boolean isReserved(String name) {
        return GLSL_RESERVED.contains(name) || externalScope.contains(name);
    }

    private void allocate(String name) {
        if (!isReserved(name) && !mapping.containsKey(name)) {
            mapping.put(name, generateName(counter++));
        }
    }

    private static String generateName(int index) {
        var sb = new StringBuilder();
        do {
            sb.insert(0, (char) ('a' + index % 26));
            index = index / 26 - 1;
        } while (index >= 0);
        return sb.toString();
    }

    @Override
    protected VariableDeclaration rewriteVariableDeclaration(VariableDeclaration d) {
        var initializer = d.initializer() == null ? null : rewriteExpr(d.initializer());
        return new VariableDeclaration(d.qualifier(), d.glslType(), mapped(d.name()), initializer);
    }

    @Override
    protected FunctionDeclaration rewriteFunctionDeclaration(FunctionDeclaration d) {
        var params = d.parameters().stream().map(this::rewriteFunctionParameter).toList();
        return new FunctionDeclaration(d.returnType(), mapped(d.name()), params, rewriteBlock(d.body()));
    }

    @Override
    protected FunctionParameter rewriteFunctionParameter(FunctionParameter p) {
        return new FunctionParameter(p.glslType(), mapped(p.name()));
    }

    @Override
    protected Statement rewriteStatement(Statement node) {
        return switch (node) {
            case LocalVariableDeclarationStatement(
                    boolean isConst, GlslType glslType, String name, Expression initializer
            ) -> {
                var init = initializer == null ? null : rewriteExpr(initializer);
                yield new LocalVariableDeclarationStatement(isConst, glslType, mapped(name), init);
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

    private String mapped(String name) {
        return mapping.getOrDefault(name, name);
    }
}
