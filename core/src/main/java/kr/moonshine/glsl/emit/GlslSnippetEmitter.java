package kr.moonshine.glsl.emit;

import kr.moonshine.glsl.ast.ShaderSnippet;
import kr.moonshine.glsl.ast.stmt.SwitchCase;

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

    public String emitSwitchCase(SwitchCase switchCase) {
        return "case " + switchCase.id() + ": " + emitBlock(switchCase.body());
    }
}
