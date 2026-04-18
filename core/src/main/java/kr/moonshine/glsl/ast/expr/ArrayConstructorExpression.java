package kr.moonshine.glsl.ast.expr;

import kr.moonshine.glsl.type.ArrayType;

import java.util.List;

public record ArrayConstructorExpression(
        ArrayType glslType,
        List<Expression> elements
) implements Expression {
}
