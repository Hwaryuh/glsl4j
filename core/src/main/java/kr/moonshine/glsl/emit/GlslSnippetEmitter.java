package kr.moonshine.glsl.emit;

import kr.moonshine.glsl.ast.ShaderSnippet;

import java.util.stream.Collectors;

public final class GlslSnippetEmitter extends BaseGlslEmitter {

    public GlslSnippetEmitter(EmitMode mode) {
        super(mode);
    }

    public String emit(ShaderSnippet snippet) {
        return snippet.statements().stream()
                .map(this::emitStatement)
                .collect(Collectors.joining(nl()));
    }
}
