package kr.moonshine.glsl.type;

public sealed interface GlslType extends FunctionReturnType
        permits ScalarType, VectorType, MatrixType, OpaqueType, ArrayType {

    String glslName();
}
