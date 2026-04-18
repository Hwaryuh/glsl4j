package kr.moonshine.glsl.ast.stmt;

import kr.moonshine.glsl.ast.expr.Expression;
import org.jetbrains.annotations.Nullable;

public record IfStatement(
        Expression condition,
        Block thenBlock,
        @Nullable Statement elseBlock
) implements Statement {
}
