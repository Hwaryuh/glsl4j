package kr.moonshine.glsl.ast.decl;

import kr.moonshine.glsl.ast.GlslNode;

public sealed interface Declaration extends GlslNode
        permits VariableDeclaration, FunctionDeclaration, StructDeclaration {
}
