package kr.moonshine.glsl.type;

public enum Vec2Type implements VectorType {
    INSTANCE;

    public int dimension() {
        return 2;
    }

    public ScalarType elementType() {
        return ScalarType.FLOAT;
    }

    public String glslName() {
        return "vec2";
    }
}
