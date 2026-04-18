package kr.moonshine.glsl.ast.expr;

import kr.moonshine.glsl.type.GlslType;

public record ArrayIndexExpression(
        Expression target,
        Expression index,
        GlslType glslType
) implements Expression {
}
