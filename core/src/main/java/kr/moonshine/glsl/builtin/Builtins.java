package kr.moonshine.glsl.builtin;

import kr.moonshine.glsl.ShaderStage;
import kr.moonshine.glsl.ast.expr.Builtin;
import kr.moonshine.glsl.type.ScalarType;
import kr.moonshine.glsl.type.Vec4Type;

import java.util.EnumSet;

public final class Builtins {

    public static final Builtin GL_POSITION = new BuiltinImpl(
            "gl_Position",
            Vec4Type.INSTANCE,
            EnumSet.of(ShaderStage.VERTEX),
            AccessMode.WRITE_ONLY
    );

    public static final Builtin GL_FRAG_COORD = new BuiltinImpl(
            "gl_FragCoord",
            Vec4Type.INSTANCE,
            EnumSet.of(ShaderStage.FRAGMENT),
            AccessMode.READ_ONLY
    );

    public static final Builtin GL_FRAG_DEPTH = new BuiltinImpl(
            "gl_FragDepth",
            ScalarType.FLOAT,
            EnumSet.of(ShaderStage.FRAGMENT),
            AccessMode.WRITE_ONLY
    );

    private Builtins() {}
}
