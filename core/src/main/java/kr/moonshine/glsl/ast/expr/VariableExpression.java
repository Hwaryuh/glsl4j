package kr.moonshine.glsl.ast.expr;

import kr.moonshine.glsl.type.GlslType;

public record VariableExpression(
        String name,
        GlslType glslType
) implements Expression {
}
