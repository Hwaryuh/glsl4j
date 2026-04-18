package kr.moonshine.glsl.type;

public sealed interface FunctionReturnType
        permits GlslType, VoidType {

    String glslName();
}
