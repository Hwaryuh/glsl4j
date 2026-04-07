package kr.moonshine.glsl.ast.stmt;

import kr.moonshine.glsl.ast.expr.Expression;
import org.jetbrains.annotations.Nullable;

public record ForStatement(
        @Nullable Statement init,
        @Nullable Expression condition,
        @Nullable Expression update,
        Block body
) implements Statement {
}
