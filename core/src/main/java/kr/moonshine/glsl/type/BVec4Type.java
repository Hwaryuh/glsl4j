package kr.moonshine.glsl.type;

public enum BVec4Type implements VectorType {
    INSTANCE;

    public int dimension() {
        return 4;
    }

    public ScalarType elementType() {
        return ScalarType.BOOL;
    }

    public String glslName() {
        return "bvec4";
    }
}
