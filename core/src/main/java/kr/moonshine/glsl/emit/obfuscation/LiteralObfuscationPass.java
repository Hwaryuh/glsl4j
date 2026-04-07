package kr.moonshine.glsl.emit.obfuscation;

import kr.moonshine.glsl.ast.ShaderUnit;
import kr.moonshine.glsl.ast.expr.BinaryExpression;
import kr.moonshine.glsl.ast.expr.Expression;
import kr.moonshine.glsl.ast.expr.FloatLiteral;
import kr.moonshine.glsl.ast.expr.IntLiteral;
import kr.moonshine.glsl.ast.expr.LiteralExpression;
import kr.moonshine.glsl.ast.expr.BinaryOperator;
import kr.moonshine.glsl.emit.ObfuscationFeature;
import kr.moonshine.glsl.emit.ObfuscationPass;
import kr.moonshine.glsl.type.ScalarType;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class LiteralObfuscationPass extends AstRewriter implements ObfuscationPass {

    @Override
    public ShaderUnit apply(ShaderUnit unit, Set<ObfuscationFeature> features) {
        if (!features.contains(ObfuscationFeature.LITERAL)) return unit;
        return rewriteUnit(unit);
    }

    @Override
    protected Expression rewriteLiteral(LiteralExpression e) {
        return switch (e) {
            case IntLiteral lit -> splitInt(lit.value());
            case FloatLiteral lit -> splitFloat(lit.value());
            default -> e;
        };
    }

    private Expression splitInt(int value) {
        if (value == 0 || value == Integer.MIN_VALUE) return new IntLiteral(value);
        BinaryOperator op = pickAdditiveOperator();
        int bound = Math.abs(value);
        int a = ThreadLocalRandom.current().nextInt(bound);
        int b = op == BinaryOperator.ADD ? value - a : value + a;
        return new BinaryExpression(new IntLiteral(a), op, new IntLiteral(b), ScalarType.INT);
    }

    private Expression splitFloat(float value) {
        if (value == 0f) return new FloatLiteral(0f);
        BinaryOperator op = pickAdditiveOperator();
        float a = ThreadLocalRandom.current().nextFloat() * Math.abs(value);
        float b = op == BinaryOperator.ADD ? value - a : value + a;
        return new BinaryExpression(new FloatLiteral(a), op, new FloatLiteral(b), ScalarType.FLOAT);
    }

    // Currently supports only addition and subtraction. Multiplication and division will be added here at a later date.
    private BinaryOperator pickAdditiveOperator() {
        return ThreadLocalRandom.current().nextBoolean() ? BinaryOperator.ADD : BinaryOperator.SUB;
    }
}
