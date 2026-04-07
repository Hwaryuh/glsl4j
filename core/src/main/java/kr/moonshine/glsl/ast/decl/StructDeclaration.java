package kr.moonshine.glsl.ast.decl;

import java.util.List;

public record StructDeclaration(
        String name,
        List<StructField> fields
) implements Declaration {
}
