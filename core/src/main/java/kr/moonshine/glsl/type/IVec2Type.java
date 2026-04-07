package kr.moonshine.glsl.type;

public enum IVec2Type implements VectorType {
    INSTANCE;

    public int dimension() {
        return 2;
    }

    public ScalarType elementType() {
        return ScalarType.INT;
    }

    public String glslName() {
        return "ivec2";
    }
}
