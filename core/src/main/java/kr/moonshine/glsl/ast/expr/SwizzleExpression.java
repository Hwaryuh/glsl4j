package kr.moonshine.glsl.ast.expr;

import kr.moonshine.glsl.type.GlslType;

public record SwizzleExpression(
        Expression target,
        String components,
        GlslType glslType
) implements Expression {
}
