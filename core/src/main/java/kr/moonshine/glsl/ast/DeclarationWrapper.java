package kr.moonshine.glsl.ast;

import kr.moonshine.glsl.ast.decl.Declaration;

public record DeclarationWrapper(
        Declaration node
) implements TopLevelNode {
}
