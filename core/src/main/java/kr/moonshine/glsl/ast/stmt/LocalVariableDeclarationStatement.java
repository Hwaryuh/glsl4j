package kr.moonshine.glsl.ast.stmt;

import kr.moonshine.glsl.ast.expr.Expression;
import kr.moonshine.glsl.type.GlslType;
import org.jetbrains.annotations.Nullable;

public record LocalVariableDeclarationStatement(
        boolean isConst,
        GlslType glslType,
        String name,
        @Nullable Expression initializer
) implements Statement {
}
