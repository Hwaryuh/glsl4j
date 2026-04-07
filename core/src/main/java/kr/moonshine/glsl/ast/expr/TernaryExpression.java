package kr.moonshine.glsl.ast.expr;

import kr.moonshine.glsl.type.GlslType;

// The glslType of `condition` must be `ScalarType.BOOL`. This is validated by `GlslValidator`.
public record TernaryExpression(
        Expression condition,
        Expression thenExpr,
        Expression elseExpr,
        GlslType glslType
) implements Expression {
}
