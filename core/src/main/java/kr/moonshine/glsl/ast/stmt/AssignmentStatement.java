package kr.moonshine.glsl.ast.stmt;

import kr.moonshine.glsl.ast.expr.Expression;

public record AssignmentStatement(
        Expression target,
        AssignmentOperator operator,
        Expression value
) implements Statement {
}
