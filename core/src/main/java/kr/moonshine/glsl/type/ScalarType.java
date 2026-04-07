package kr.moonshine.glsl.type;

public enum ScalarType implements GlslType {
    FLOAT("float"),
    INT("int"),
    BOOL("bool"),
    ;

    private final String glslName;

    ScalarType(String glslName) {
        this.glslName = glslName;
    }

    @Override
    public String glslName() {
        return glslName;
    }
}
