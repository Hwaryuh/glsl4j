package kr.moonshine.glsl.type;

public enum Mat3x4Type implements MatrixType {
    INSTANCE;

    public int columns() {
        return 3;
    }

    public int rows() {
        return 4;
    }

    public String glslName() {
        return "mat3x4";
    }
}
