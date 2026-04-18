package kr.moonshine.glsl.emit;

import kr.moonshine.glsl.ast.DeclarationWrapper;
import kr.moonshine.glsl.ast.PreprocessorWrapper;
import kr.moonshine.glsl.ast.ShaderUnit;
import kr.moonshine.glsl.ast.TopLevelNode;
import kr.moonshine.glsl.ast.decl.Declaration;
import kr.moonshine.glsl.ast.decl.FunctionDeclaration;
import kr.moonshine.glsl.ast.decl.StorageQualifier;
import kr.moonshine.glsl.ast.decl.StructDeclaration;
import kr.moonshine.glsl.ast.decl.VariableDeclaration;
import kr.moonshine.glsl.dialect.GlslDialect;
import kr.moonshine.glsl.type.FunctionReturnType;

import java.util.stream.Collectors;

public final class GlslEmitter extends BaseGlslEmitter {

    private final GlslDialect dialect;

    public GlslEmitter(GlslDialect dialect, EmitMode mode) {
        super(mode);
        this.dialect = dialect;
    }

    public String emit(ShaderUnit unit) {
        var sb = new StringBuilder();
        sb.append("#version ").append(unit.version().versionString()).append('\n');
        for (var node : unit.nodes()) {
            sb.append(emitTopLevel(node)).append('\n');
        }
        return sb.toString();
    }

    private String emitTopLevel(TopLevelNode node) {
        return switch (node) {
            case PreprocessorWrapper w -> dialect.emitDirective(w.node());
            case DeclarationWrapper w -> emitDeclaration(w.node());
        };
    }

    private String emitDeclaration(Declaration node) {
        return switch (node) {
            case VariableDeclaration d -> emitVariableDecl(d);
            case FunctionDeclaration d -> emitFunctionDecl(d);
            case StructDeclaration d -> emitStructDecl(d);
        };
    }

    private String emitVariableDecl(VariableDeclaration d) {
        var sb = new StringBuilder();
        if (d.qualifier() != StorageQualifier.LOCAL) {
            sb.append(dialect.emitQualifier(d.qualifier())).append(' ');
        }
        sb.append(d.glslType().glslName()).append(' ').append(d.name());
        if (d.initializer() != null) {
            sb.append(" = ").append(emitExpr(d.initializer()));
        }
        sb.append(';');
        return sb.toString();
    }

    private String emitFunctionDecl(FunctionDeclaration d) {
        var sb = new StringBuilder();
        sb.append(emitReturnType(d.returnType())).append(' ').append(d.name()).append('(');
        var params = d.parameters().stream()
                .map(p -> p.glslType().glslName() + ' ' + p.name())
                .collect(Collectors.joining(", "));
        sb.append(params).append(") ");
        sb.append(emitBlock(d.body()));
        return sb.toString();
    }

    private String emitReturnType(FunctionReturnType returnType) {
        return returnType.glslName();
    }

    private String emitStructDecl(StructDeclaration d) {
        var sb = new StringBuilder();
        sb.append("struct ").append(d.name()).append(sep()).append("{").append(nl());
        for (var field : d.fields()) {
            sb.append(indent(field.glslType().glslName() + ' ' + field.name() + ';')).append(nl());
        }
        sb.append("};");
        return sb.toString();
    }
}
