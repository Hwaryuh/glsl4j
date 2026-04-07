package kr.moonshine.glsl.ast.stmt;

import kr.moonshine.glsl.ast.expr.Expression;

public record ExpressionStatement(
        Expression expression
) implements Statement {
}
