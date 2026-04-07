package kr.moonshine.glsl.validate;

import kr.moonshine.glsl.ShaderStage;
import kr.moonshine.glsl.ast.expr.Builtin;
import kr.moonshine.glsl.builtin.AccessMode;

public final class BuiltinValidator {

    private BuiltinValidator() {
    }

    public static void validateRead(Builtin builtin, ShaderStage stage) {
        validateStage(builtin, stage);
        if (builtin.access() == AccessMode.WRITE_ONLY) {
            throw new GlslValidationException(
                    "Builtin '" + builtin.name() + "' is write-only and cannot be read"
            );
        }
    }

    public static void validateWrite(Builtin builtin, ShaderStage stage) {
        validateStage(builtin, stage);
        if (builtin.access() == AccessMode.READ_ONLY) {
            throw new GlslValidationException(
                    "Builtin '" + builtin.name() + "' is read-only and cannot be written"
            );
        }
    }

    private static void validateStage(Builtin builtin, ShaderStage stage) {
        if (!builtin.supportedStages().contains(stage)) {
            throw new GlslValidationException(
                    "Builtin '" + builtin.name() + "' is not available in " + stage + " stage"
            );
        }
    }
}
