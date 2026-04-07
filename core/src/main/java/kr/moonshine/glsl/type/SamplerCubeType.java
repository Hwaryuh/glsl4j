package kr.moonshine.glsl.type;

public enum SamplerCubeType implements SamplerType {
    INSTANCE;

    public String glslName() {
        return "samplerCube";
    }
}
