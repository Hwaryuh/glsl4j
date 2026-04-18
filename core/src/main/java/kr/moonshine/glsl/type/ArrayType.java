package kr.moonshine.glsl.type;

public record ArrayType(
        GlslType elementType,
        int size
) implements GlslType {

    @Override
    public String glslName() {
        return elementType.glslName() + "[" + size + "]";
    }
}
