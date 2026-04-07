package kr.moonshine.glsl.type;

public enum Mat3Type implements MatrixType {
    INSTANCE;

    public int columns() {
        return 3;
    }

    public int rows() {
        return 3;
    }

    public String glslName() {
        return "mat3";
    }
}
