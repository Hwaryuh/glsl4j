package kr.moonshine.glsl.type;

public sealed interface GlslType
        permits ScalarType, VectorType, MatrixType, OpaqueType {

    String glslName();
}
