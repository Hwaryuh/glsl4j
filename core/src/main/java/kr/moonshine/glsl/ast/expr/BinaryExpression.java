package kr.moonshine.glsl.ast.expr;

import kr.moonshine.glsl.type.GlslType;

public record BinaryExpression(
        Expression left,
        BinaryOperator operator,
        Expression right,
        GlslType glslType
) implements Expression {
}
