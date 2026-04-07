package kr.moonshine.glsl.ast.expr;

import kr.moonshine.glsl.type.GlslType;
import kr.moonshine.glsl.type.ScalarType;

public record FloatLiteral(float value) implements LiteralExpression {

    public GlslType glslType() {
        return ScalarType.FLOAT;
    }
}
