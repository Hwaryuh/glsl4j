package kr.moonshine.glsl.type;

public enum Mat2x4Type implements MatrixType {
    INSTANCE;

    public int columns() {
        return 2;
    }

    public int rows() {
        return 4;
    }

    public String glslName() {
        return "mat2x4";
    }
}
