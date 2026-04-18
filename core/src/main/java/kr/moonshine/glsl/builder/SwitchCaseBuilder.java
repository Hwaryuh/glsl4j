package kr.moonshine.glsl.builder;

import kr.moonshine.glsl.ast.expr.Expression;
import kr.moonshine.glsl.ast.stmt.AssignmentOperator;
import kr.moonshine.glsl.ast.stmt.Block;
import kr.moonshine.glsl.ast.stmt.Statement;
import kr.moonshine.glsl.ast.stmt.SwitchCase;
import kr.moonshine.glsl.type.GlslType;
import org.jetbrains.annotations.Nullable;

public final class SwitchCaseBuilder {

    private final int id;
    private final BlockBuilder blockBuilder = BlockBuilder.create();
    private boolean autoBreak = true;

    private SwitchCaseBuilder(int id) {
        this.id = id;
    }

    public static SwitchCaseBuilder of(int id) {
        return new SwitchCaseBuilder(id);
    }

    public SwitchCaseBuilder assign(Expression target, Expression value) {
        blockBuilder.assign(target, value);
        return this;
    }

    public SwitchCaseBuilder compoundAssign(Expression target, AssignmentOperator operator, Expression value) {
        blockBuilder.compoundAssign(target, operator, value);
        return this;
    }

    public SwitchCaseBuilder local(GlslType type, String name, Expression initializer) {
        blockBuilder.local(type, name, initializer);
        return this;
    }

    public SwitchCaseBuilder local(GlslType type, String name) {
        blockBuilder.local(type, name);
        return this;
    }

    public SwitchCaseBuilder constLocal(GlslType type, String name, Expression initializer) {
        blockBuilder.constLocal(type, name, initializer);
        return this;
    }

    public SwitchCaseBuilder stmt(Statement statement) {
        blockBuilder.stmt(statement);
        return this;
    }

    public SwitchCaseBuilder expr(Expression expression) {
        blockBuilder.expr(expression);
        return this;
    }

    public SwitchCaseBuilder ifStmt(Expression condition, Block thenBlock) {
        blockBuilder.ifStmt(condition, thenBlock);
        return this;
    }

    public SwitchCaseBuilder ifStmt(Expression condition, Block thenBlock, Block elseBlock) {
        blockBuilder.ifStmt(condition, thenBlock, elseBlock);
        return this;
    }

    public SwitchCaseBuilder ifStmt(Expression condition, Block thenBlock, Statement elseStatement) {
        blockBuilder.ifStmt(condition, thenBlock, elseStatement);
        return this;
    }

    public SwitchCaseBuilder forStmt(@Nullable Statement init, @Nullable Expression condition, @Nullable Expression update, Block body) {
        blockBuilder.forStmt(init, condition, update, body);
        return this;
    }

    public SwitchCaseBuilder macroCall(String name, Expression... arguments) {
        blockBuilder.macroCall(name, arguments);
        return this;
    }

    public SwitchCaseBuilder voidCall(String name, Expression... arguments) {
        blockBuilder.voidCall(name, arguments);
        return this;
    }

    public SwitchCaseBuilder noBreak() {
        this.autoBreak = false;
        return this;
    }

    public SwitchCase build() {
        if (autoBreak) blockBuilder.breakStmt();
        return new SwitchCase(id, blockBuilder.build());
    }
}
