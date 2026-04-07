package kr.moonshine.glsl.type;

public enum Mat3x2Type implements MatrixType {
    INSTANCE;

    public int columns() {
        return 3;
    }

    public int rows() {
        return 2;
    }

    public String glslName() {
        return "mat3x2";
    }
}
