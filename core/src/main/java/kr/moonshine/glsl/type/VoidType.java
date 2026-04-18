package kr.moonshine.glsl.type;

public enum VoidType implements FunctionReturnType {
    INSTANCE;

    public String glslName() {
        return "void";
    }
}
