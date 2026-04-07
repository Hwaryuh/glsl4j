package kr.moonshine.glsl.type;

public enum BVec3Type implements VectorType {
    INSTANCE;

    public int dimension() {
        return 3;
    }

    public ScalarType elementType() {
        return ScalarType.BOOL;
    }

    public String glslName() {
        return "bvec3";
    }
}
