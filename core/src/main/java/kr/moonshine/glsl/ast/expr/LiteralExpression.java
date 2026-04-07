package kr.moonshine.glsl.ast.expr;

public sealed interface LiteralExpression extends Expression
        permits FloatLiteral, IntLiteral, BoolLiteral {
}
