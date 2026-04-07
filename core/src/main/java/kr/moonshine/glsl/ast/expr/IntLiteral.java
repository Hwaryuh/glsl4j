package kr.moonshine.glsl.ast.expr;

import kr.moonshine.glsl.type.GlslType;
import kr.moonshine.glsl.type.ScalarType;

public record IntLiteral(int value) implements LiteralExpression {

    public GlslType glslType() {
        return ScalarType.INT;
    }
}
