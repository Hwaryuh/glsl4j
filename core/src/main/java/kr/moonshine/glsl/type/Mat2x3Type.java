package kr.moonshine.glsl.type;

public enum Mat2x3Type implements MatrixType {
    INSTANCE;

    public int columns() {
        return 2;
    }

    public int rows() {
        return 3;
    }

    public String glslName() {
        return "mat2x3";
    }
}
