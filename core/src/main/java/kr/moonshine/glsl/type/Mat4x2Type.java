package kr.moonshine.glsl.type;

public enum Mat4x2Type implements MatrixType {
    INSTANCE;

    public int columns() {
        return 4;
    }

    public int rows() {
        return 2;
    }

    public String glslName() {
        return "mat4x2";
    }
}
