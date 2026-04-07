package kr.moonshine.glsl.dialect;

import kr.moonshine.glsl.ast.decl.StorageQualifier;

public enum VanillaGlsl330Dialect implements GlslDialect {
    INSTANCE;

    @Override
    public String emitQualifier(StorageQualifier qualifier) {
        return switch (qualifier) {
            case IN -> "in";
            case OUT -> "out";
            case UNIFORM -> "uniform";
            case CONST -> "const";
            case LOCAL -> "";
        };
    }
}
