package kr.moonshine.glsl.ast.expr;

public enum UnaryOperator {
    NEGATE("-"),
    NOT("!"),
    BIT_NOT("~");

    private final String symbol;

    UnaryOperator(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }
}
