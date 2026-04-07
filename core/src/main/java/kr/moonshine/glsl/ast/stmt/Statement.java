package kr.moonshine.glsl.ast.stmt;

import kr.moonshine.glsl.ast.GlslNode;

public sealed interface Statement extends GlslNode
        permits AssignmentStatement, ExpressionStatement, ReturnStatement,
        DiscardStatement, IfStatement, ForStatement, WhileStatement,
        Block, BreakStatement, ContinueStatement,
        LocalVariableDeclarationStatement, MacroInvocationStatement,
        IntegerSwitchStatement {
}
