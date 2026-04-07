package kr.moonshine.glsl.type;

public sealed interface VectorType extends GlslType
        permits Vec2Type, Vec3Type, Vec4Type,
        IVec2Type, IVec3Type, IVec4Type,
        BVec2Type, BVec3Type, BVec4Type {

    int dimension();

    ScalarType elementType();
}
