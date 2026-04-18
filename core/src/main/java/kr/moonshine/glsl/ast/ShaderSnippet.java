package kr.moonshine.glsl.ast;

import kr.moonshine.glsl.ast.stmt.Statement;

import java.util.List;

public record ShaderSnippet(
        List<Statement> statements
) {
}
