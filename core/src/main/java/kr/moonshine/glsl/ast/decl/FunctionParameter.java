package kr.moonshine.glsl.ast.decl;

import kr.moonshine.glsl.type.GlslType;

public record FunctionParameter(
        GlslType glslType,
        String name
) {
}
