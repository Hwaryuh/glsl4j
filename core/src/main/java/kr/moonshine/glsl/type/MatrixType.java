package kr.moonshine.glsl.type;

public sealed interface MatrixType extends GlslType
        permits Mat2Type, Mat3Type, Mat4Type,
        Mat2x3Type, Mat2x4Type,
        Mat3x2Type, Mat3x4Type,
        Mat4x2Type, Mat4x3Type {

    int columns();

    int rows();
}
