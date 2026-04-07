package kr.moonshine.glsl.type;

public enum BVec2Type implements VectorType {
    INSTANCE;

    public int dimension() {
        return 2;
    }

    public ScalarType elementType() {
        return ScalarType.BOOL;
    }

    public String glslName() {
        return "bvec2";
    }
}
