package kr.moonshine.glsl.ast.decl;

import kr.moonshine.glsl.ast.stmt.Block;
import kr.moonshine.glsl.type.GlslType;

import java.util.List;

public record FunctionDeclaration(
        GlslType returnType,
        String name,
        List<FunctionParameter> parameters,
        Block body
) implements Declaration {
}
