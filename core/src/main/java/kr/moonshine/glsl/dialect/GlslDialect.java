package kr.moonshine.glsl.dialect;

import kr.moonshine.glsl.ast.decl.StorageQualifier;
import kr.moonshine.glsl.preprocessor.DefineDirective;
import kr.moonshine.glsl.preprocessor.IfdefDirective;
import kr.moonshine.glsl.preprocessor.IfndefDirective;
import kr.moonshine.glsl.preprocessor.IncludeDirective;
import kr.moonshine.glsl.preprocessor.MojImportDirective;
import kr.moonshine.glsl.preprocessor.PreprocessorNode;
import kr.moonshine.glsl.preprocessor.UndefDirective;

public interface GlslDialect {

    String emitQualifier(StorageQualifier qualifier);

    default String emitDirective(PreprocessorNode node) {
        return switch (node) {
            case DefineDirective d ->
                    d.value() == null ? "#define " + d.name() : "#define " + d.name() + " " + d.value();
            case UndefDirective d -> "#undef " + d.name();
            case IfdefDirective d -> emitIfdef(d);
            case IfndefDirective d -> emitIfndef(d);
            case IncludeDirective d -> "#include \"" + d.path() + "\"";
            case MojImportDirective d ->
                    throw new UnsupportedDialectException("MojImportDirective is not supported in " + this);
        };
    }

    default String emitIfdef(IfdefDirective d) {
        var sb = new StringBuilder();
        sb.append("#ifdef ").append(d.name()).append('\n');
        d.thenNodes().forEach(n -> sb.append(emitDirective(n)).append('\n'));
        if (!d.elseNodes().isEmpty()) {
            sb.append("#else\n");
            d.elseNodes().forEach(n -> sb.append(emitDirective(n)).append('\n'));
        }
        sb.append("#endif");
        return sb.toString();
    }

    default String emitIfndef(IfndefDirective d) {
        var sb = new StringBuilder();
        sb.append("#ifndef ").append(d.name()).append('\n');
        d.thenNodes().forEach(n -> sb.append(emitDirective(n)).append('\n'));
        if (!d.elseNodes().isEmpty()) {
            sb.append("#else\n");
            d.elseNodes().forEach(n -> sb.append(emitDirective(n)).append('\n'));
        }
        sb.append("#endif");
        return sb.toString();
    }
}
