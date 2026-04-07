package kr.moonshine.glsl.type;

public enum Vec3Type implements VectorType {
    INSTANCE;

    public int dimension() {
        return 3;
    }

    public ScalarType elementType() {
        return ScalarType.FLOAT;
    }

    public String glslName() {
        return "vec3";
    }
}
