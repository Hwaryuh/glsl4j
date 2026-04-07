package kr.moonshine.glsl.ast.expr;

public enum BinaryOperator {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    MOD("%"),
    EQ("=="),
    NEQ("!="),
    LT("<"),
    GT(">"),
    LTE("<="),
    GTE(">="),
    AND("&&"),
    OR("||"),
    BIT_AND("&"),
    BIT_OR("|"),
    BIT_XOR("^"),
    SHL("<<"),
    SHR(">>");

    private final String symbol;

    BinaryOperator(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }
}
