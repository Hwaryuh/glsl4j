package kr.moonshine.glsl.ast.stmt;

import java.util.List;

public record Block(
        List<Statement> statements
) implements Statement {
}
