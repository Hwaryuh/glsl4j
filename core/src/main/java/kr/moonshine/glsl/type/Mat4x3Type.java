package kr.moonshine.glsl.type;

public enum Mat4x3Type implements MatrixType {
    INSTANCE;

    public int columns() {
        return 4;
    }

    public int rows() {
        return 3;
    }

    public String glslName() {
        return "mat4x3";
    }
}
