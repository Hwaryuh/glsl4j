package kr.moonshine.glsl.dialect;

import kr.moonshine.glsl.ast.decl.StorageQualifier;

public enum VanillaGlsl150Dialect implements GlslDialect {
    INSTANCE;

    @Override
    public String emitQualifier(StorageQualifier qualifier) {
        return switch (qualifier) {
            case IN -> "attribute";
            case OUT -> "varying";
            case UNIFORM -> "uniform";
            case CONST -> "const";
            case LOCAL -> "";
        };
    }
}
