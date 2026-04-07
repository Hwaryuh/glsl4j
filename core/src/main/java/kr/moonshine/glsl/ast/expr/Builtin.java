package kr.moonshine.glsl.ast.expr;

import kr.moonshine.glsl.ShaderStage;
import kr.moonshine.glsl.builtin.AccessMode;
import kr.moonshine.glsl.type.GlslType;

import java.util.Set;

public non-sealed interface Builtin extends Expression {
    String name();

    @Override
    GlslType glslType();

    Set<ShaderStage> supportedStages();

    AccessMode access();
}
