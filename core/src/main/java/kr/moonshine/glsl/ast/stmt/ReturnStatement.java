package kr.moonshine.glsl.ast.stmt;

import kr.moonshine.glsl.ast.expr.Expression;
import org.jetbrains.annotations.Nullable;

public record ReturnStatement(
        @Nullable Expression value
) implements Statement {
}
