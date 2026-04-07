package kr.moonshine.glsl.ast.stmt;

import kr.moonshine.glsl.ast.expr.Expression;

import java.util.List;

public record MacroInvocationStatement(
        String name,
        List<Expression> arguments
) implements Statement {
}
