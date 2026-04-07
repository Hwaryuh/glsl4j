package kr.moonshine.glsl.ast.decl;

import kr.moonshine.glsl.ast.expr.Expression;
import kr.moonshine.glsl.type.GlslType;
import org.jetbrains.annotations.Nullable;

public record VariableDeclaration(
        StorageQualifier qualifier,
        GlslType glslType,
        String name,
        @Nullable Expression initializer
) implements Declaration {
}
