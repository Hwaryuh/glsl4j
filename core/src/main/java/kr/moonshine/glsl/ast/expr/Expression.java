package kr.moonshine.glsl.ast.expr;

import kr.moonshine.glsl.ast.GlslNode;
import kr.moonshine.glsl.type.GlslType;

public sealed interface Expression extends GlslNode
        permits LiteralExpression, VariableExpression, BinaryExpression,
        FunctionCallExpression, SwizzleExpression, UnaryExpression,
        TernaryExpression, Builtin,
        ArrayConstructorExpression, ArrayIndexExpression, MacroCallExpression {

    GlslType glslType();
}
