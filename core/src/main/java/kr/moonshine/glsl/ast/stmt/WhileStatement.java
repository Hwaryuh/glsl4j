package kr.moonshine.glsl.ast.stmt;

import kr.moonshine.glsl.ast.expr.Expression;

public record WhileStatement(
        Expression condition,
        Block body
) implements Statement {
}
