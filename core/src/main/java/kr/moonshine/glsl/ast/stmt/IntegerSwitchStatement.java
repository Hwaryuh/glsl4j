package kr.moonshine.glsl.ast.stmt;

import kr.moonshine.glsl.ast.expr.Expression;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record IntegerSwitchStatement(
        Expression selector,
        List<SwitchCase> cases,
        @Nullable Block defaultCase
) implements Statement {
}
