package kr.moonshine.glsl.type;

public enum Sampler2DType implements SamplerType {
    INSTANCE;

    public String glslName() {
        return "sampler2D";
    }
}
