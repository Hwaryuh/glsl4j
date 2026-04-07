package kr.moonshine.glsl.type;

public enum Mat2Type implements MatrixType {
    INSTANCE;

    public int columns() {
        return 2;
    }

    public int rows() {
        return 2;
    }

    public String glslName() {
        return "mat2";
    }
}
