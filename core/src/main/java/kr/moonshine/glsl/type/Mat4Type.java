package kr.moonshine.glsl.type;

public enum Mat4Type implements MatrixType {
    INSTANCE;

    public int columns() {
        return 4;
    }

    public int rows() {
        return 4;
    }

    public String glslName() {
        return "mat4";
    }
}
