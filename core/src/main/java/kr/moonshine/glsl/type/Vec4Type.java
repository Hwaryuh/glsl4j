package kr.moonshine.glsl.type;

public enum Vec4Type implements VectorType {
    INSTANCE;

    public int dimension() {
        return 4;
    }

    public ScalarType elementType() {
        return ScalarType.FLOAT;
    }

    public String glslName() {
        return "vec4";
    }
}
