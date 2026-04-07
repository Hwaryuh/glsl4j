package kr.moonshine.glsl.ast.expr;

import kr.moonshine.glsl.type.GlslType;

import java.util.List;

public record FunctionCallExpression(
        String name,
        List<Expression> arguments,
        GlslType glslType
) implements Expression {
}
