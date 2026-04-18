package kr.moonshine.glsl.ast.stmt;

public enum AssignmentOperator {
    ASSIGN("="),
    ADD_ASSIGN("+="),
    SUB_ASSIGN("-="),
    MUL_ASSIGN("*="),
    DIV_ASSIGN("/="),
    MOD_ASSIGN("%="),
    BIT_AND_ASSIGN("&="),
    BIT_OR_ASSIGN("|="),
    BIT_XOR_ASSIGN("^="),
    SHL_ASSIGN("<<="),
    SHR_ASSIGN(">>="),
    ;

    private final String symbol;

    AssignmentOperator(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }
}
