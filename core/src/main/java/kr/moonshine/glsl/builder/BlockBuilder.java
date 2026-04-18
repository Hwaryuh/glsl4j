package kr.moonshine.glsl.builder;

import com.google.common.collect.Lists;
import kr.moonshine.glsl.ast.expr.Expression;
import kr.moonshine.glsl.ast.stmt.AssignmentOperator;
import kr.moonshine.glsl.ast.stmt.AssignmentStatement;
import kr.moonshine.glsl.ast.stmt.Block;
import kr.moonshine.glsl.ast.stmt.BreakStatement;
import kr.moonshine.glsl.ast.stmt.DiscardStatement;
import kr.moonshine.glsl.ast.stmt.ExpressionStatement;
import kr.moonshine.glsl.ast.stmt.ForStatement;
import kr.moonshine.glsl.ast.stmt.IfStatement;
import kr.moonshine.glsl.ast.stmt.LocalVariableDeclarationStatement;
import kr.moonshine.glsl.ast.stmt.MacroInvocationStatement;
import kr.moonshine.glsl.ast.stmt.ReturnStatement;
import kr.moonshine.glsl.ast.stmt.Statement;
import kr.moonshine.glsl.ast.stmt.VoidFunctionCallStatement;
import kr.moonshine.glsl.type.GlslType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class BlockBuilder {

    private final List<Statement> statements = Lists.newArrayList();

    private BlockBuilder() {
    }

    public static BlockBuilder create() {
        return new BlockBuilder();
    }

    public BlockBuilder assign(Expression target, Expression value) {
        statements.add(new AssignmentStatement(target, AssignmentOperator.ASSIGN, value));
        return this;
    }

    public BlockBuilder compoundAssign(Expression target, AssignmentOperator operator, Expression value) {
        if (operator == AssignmentOperator.ASSIGN) {
            throw new IllegalArgumentException("Use assign() for simple assignment");
        }
        statements.add(new AssignmentStatement(target, operator, value));
        return this;
    }

    public BlockBuilder local(GlslType type, String name, Expression initializer) {
        statements.add(new LocalVariableDeclarationStatement(false, type, name, initializer));
        return this;
    }

    public BlockBuilder local(GlslType type, String name) {
        statements.add(new LocalVariableDeclarationStatement(false, type, name, null));
        return this;
    }

    public BlockBuilder constLocal(GlslType type, String name, Expression initializer) {
        statements.add(new LocalVariableDeclarationStatement(true, type, name, initializer));
        return this;
    }

    public BlockBuilder stmt(Statement statement) {
        statements.add(statement);
        return this;
    }

    public BlockBuilder expr(Expression expression) {
        statements.add(new ExpressionStatement(expression));
        return this;
    }

    public BlockBuilder ifStmt(Expression condition, Block thenBlock) {
        statements.add(new IfStatement(condition, thenBlock, null));
        return this;
    }

    public BlockBuilder ifStmt(Expression condition, Block thenBlock, Block elseBlock) {
        statements.add(new IfStatement(condition, thenBlock, elseBlock));
        return this;
    }

    public BlockBuilder ifStmt(Expression condition, Block thenBlock, Statement elseStatement) {
        statements.add(new IfStatement(condition, thenBlock, elseStatement));
        return this;
    }

    public BlockBuilder forStmt(@Nullable Statement init, @Nullable Expression condition, @Nullable Expression update, Block body) {
        statements.add(new ForStatement(init, condition, update, body));
        return this;
    }

    public BlockBuilder macroCall(String name, Expression... arguments) {
        statements.add(new MacroInvocationStatement(name, Lists.newArrayList(arguments)));
        return this;
    }

    public BlockBuilder voidCall(String name, Expression... arguments) {
        statements.add(new VoidFunctionCallStatement(name, Lists.newArrayList(arguments)));
        return this;
    }

    public BlockBuilder breakStmt() {
        statements.add(new BreakStatement());
        return this;
    }

    public BlockBuilder ret(@Nullable Expression value) {
        statements.add(new ReturnStatement(value));
        return this;
    }

    public BlockBuilder ret() {
        statements.add(new ReturnStatement(null));
        return this;
    }

    public BlockBuilder discard() {
        statements.add(new DiscardStatement());
        return this;
    }

    public Block build() {
        return new Block(List.copyOf(statements));
    }
}
