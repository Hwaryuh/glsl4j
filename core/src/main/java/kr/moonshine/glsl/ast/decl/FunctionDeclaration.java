package kr.moonshine.glsl.ast.decl;

import kr.moonshine.glsl.ast.stmt.Block;
import kr.moonshine.glsl.type.FunctionReturnType;

import java.util.List;

public record FunctionDeclaration(
        FunctionReturnType returnType,
        String name,
        List<FunctionParameter> parameters,
        Block body
) implements Declaration {
}
