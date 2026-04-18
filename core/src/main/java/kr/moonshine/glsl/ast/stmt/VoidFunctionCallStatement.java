package kr.moonshine.glsl.ast.stmt;

import java.util.List;
import kr.moonshine.glsl.ast.expr.Expression;

public record VoidFunctionCallStatement(
        String name,
        List<Expression> arguments
) implements Statement {
}
