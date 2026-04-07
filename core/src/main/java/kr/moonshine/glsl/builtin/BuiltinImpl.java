package kr.moonshine.glsl.builtin;

import kr.moonshine.glsl.ShaderStage;
import kr.moonshine.glsl.ast.expr.Builtin;
import kr.moonshine.glsl.type.GlslType;

import java.util.Set;

public record BuiltinImpl(
        String name,
        GlslType glslType,
        Set<ShaderStage> supportedStages,
        AccessMode access
) implements Builtin {
}
