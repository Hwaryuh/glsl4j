package kr.moonshine.glsl.dialect;

import kr.moonshine.glsl.ast.decl.StorageQualifier;
import kr.moonshine.glsl.preprocessor.MojImportDirective;
import kr.moonshine.glsl.preprocessor.PreprocessorNode;

public enum MojangCoreShaderDialect implements GlslDialect {
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

    @Override
    public String emitDirective(PreprocessorNode node) {
        if (node instanceof MojImportDirective(String path)) {
            return "#moj_import <" + path + ">";
        }
        return GlslDialect.super.emitDirective(node);
    }
}
