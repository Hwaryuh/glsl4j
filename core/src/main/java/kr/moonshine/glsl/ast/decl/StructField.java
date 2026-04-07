package kr.moonshine.glsl.ast.decl;

import kr.moonshine.glsl.type.GlslType;

public record StructField(
        GlslType glslType,
        String name
) {
}
