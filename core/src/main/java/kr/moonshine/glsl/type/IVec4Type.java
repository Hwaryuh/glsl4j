package kr.moonshine.glsl.type;

public enum IVec4Type implements VectorType {
    INSTANCE;

    public int dimension() {
        return 4;
    }

    public ScalarType elementType() {
        return ScalarType.INT;
    }

    public String glslName() {
        return "ivec4";
    }
}
