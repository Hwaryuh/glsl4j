package kr.moonshine.glsl.type;

public sealed interface SamplerType extends OpaqueType
        permits Sampler2DType, SamplerCubeType {
}
