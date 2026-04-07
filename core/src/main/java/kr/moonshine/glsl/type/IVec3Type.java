package kr.moonshine.glsl.type;

public enum IVec3Type implements VectorType {
    INSTANCE;

    public int dimension() {
        return 3;
    }

    public ScalarType elementType() {
        return ScalarType.INT;
    }

    public String glslName() {
        return "ivec3";
    }
}
