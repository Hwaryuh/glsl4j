package kr.moonshine.glsl.ast.expr;

import kr.moonshine.glsl.type.GlslType;

public record UnaryExpression(
        UnaryOperator operator,
        Expression operand,
        GlslType glslType
) implements Expression {
}
