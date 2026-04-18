package kr.moonshine.glsl.ast.expr;

public enum BinaryOperator {
    MUL("*",  11),
    DIV("/",  11),
    MOD("%",  11),
    ADD("+",  10),
    SUB("-",  10),
    SHL("<<",  9),
    SHR(">>",  9),
    LT("<",    8),
    GT(">",    8),
    LTE("<=",  8),
    GTE(">=",  8),
    EQ("==",   7),
    NEQ("!=",  7),
    BIT_AND("&",  6),
    BIT_XOR("^",  5),
    BIT_OR("|",   4),
    AND("&&",     3),
    OR("||",      2),
    ;

    private final String symbol;
    private final int precedence;

    BinaryOperator(String symbol, int precedence) {
        this.symbol = symbol;
        this.precedence = precedence;
    }

    public String symbol() {
        return symbol;
    }

    public int precedence() {
        return precedence;
    }
}
