package kr.moonshine.glsl.ast.expr;

import com.google.common.collect.Lists;
import kr.moonshine.glsl.type.GlslType;
import kr.moonshine.glsl.type.ScalarType;
import kr.moonshine.glsl.type.Vec2Type;
import kr.moonshine.glsl.type.Vec3Type;
import kr.moonshine.glsl.type.Vec4Type;
import kr.moonshine.glsl.validate.GlslTypeException;
import kr.moonshine.glsl.validate.TypeResolver;

@SuppressWarnings("unused")
public final class Expressions {

    private Expressions() {
    }

    public static BinaryExpression add(Expression left, Expression right) {
        return binary(BinaryOperator.ADD, left, right);
    }

    public static BinaryExpression sub(Expression left, Expression right) {
        return binary(BinaryOperator.SUB, left, right);
    }

    public static BinaryExpression mul(Expression left, Expression right) {
        return binary(BinaryOperator.MUL, left, right);
    }

    public static BinaryExpression div(Expression left, Expression right) {
        return binary(BinaryOperator.DIV, left, right);
    }

    public static BinaryExpression mod(Expression left, Expression right) {
        return binary(BinaryOperator.MOD, left, right);
    }

    public static BinaryExpression eq(Expression left, Expression right) {
        return binary(BinaryOperator.EQ, left, right);
    }

    public static BinaryExpression neq(Expression left, Expression right) {
        return binary(BinaryOperator.NEQ, left, right);
    }

    public static BinaryExpression lt(Expression left, Expression right) {
        return binary(BinaryOperator.LT, left, right);
    }

    public static BinaryExpression gt(Expression left, Expression right) {
        return binary(BinaryOperator.GT, left, right);
    }

    public static BinaryExpression lte(Expression left, Expression right) {
        return binary(BinaryOperator.LTE, left, right);
    }

    public static BinaryExpression gte(Expression left, Expression right) {
        return binary(BinaryOperator.GTE, left, right);
    }

    public static BinaryExpression and(Expression left, Expression right) {
        return binary(BinaryOperator.AND, left, right);
    }

    public static BinaryExpression or(Expression left, Expression right) {
        return binary(BinaryOperator.OR, left, right);
    }

    public static BinaryExpression bitAnd(Expression left, Expression right) {
        return binary(BinaryOperator.BIT_AND, left, right);
    }

    public static BinaryExpression bitOr(Expression left, Expression right) {
        return binary(BinaryOperator.BIT_OR, left, right);
    }

    public static BinaryExpression bitXor(Expression left, Expression right) {
        return binary(BinaryOperator.BIT_XOR, left, right);
    }

    public static BinaryExpression shl(Expression left, Expression right) {
        return binary(BinaryOperator.SHL, left, right);
    }

    public static BinaryExpression shr(Expression left, Expression right) {
        return binary(BinaryOperator.SHR, left, right);
    }

    public static UnaryExpression negate(Expression operand) {
        GlslType type = operand.glslType();
        if (type == ScalarType.BOOL) {
            throw new GlslTypeException("negate() is not applicable to bool");
        }
        return new UnaryExpression(UnaryOperator.NEGATE, operand, type);
    }

    public static UnaryExpression not(Expression operand) {
        if (operand.glslType() != ScalarType.BOOL) {
            throw new GlslTypeException("not() requires bool operand");
        }
        return new UnaryExpression(UnaryOperator.NOT, operand, ScalarType.BOOL);
    }

    public static UnaryExpression bitNot(Expression operand) {
        if (operand.glslType() != ScalarType.INT) {
            throw new GlslTypeException("bitNot() requires int operand");
        }
        return new UnaryExpression(UnaryOperator.BIT_NOT, operand, ScalarType.INT);
    }

    public static SwizzleExpression swizzle(Expression target, String components, GlslType resultType) {
        return new SwizzleExpression(target, components, resultType);
    }

    public static FunctionCallExpression call(String name, GlslType returnType, Expression... arguments) {
        return new FunctionCallExpression(name, Lists.newArrayList(arguments), returnType);
    }

    public static TernaryExpression ternary(Expression condition, Expression thenExpr, Expression elseExpr) {
        if (condition.glslType() != ScalarType.BOOL) {
            throw new GlslTypeException("Ternary condition must be bool");
        }
        if (!thenExpr.glslType().equals(elseExpr.glslType())) {
            throw new GlslTypeException("Ternary branches must have same type");
        }
        return new TernaryExpression(condition, thenExpr, elseExpr, thenExpr.glslType());
    }

    public static FloatLiteral floatLit(float value) {
        return new FloatLiteral(value);
    }

    public static IntLiteral intLit(int value) {
        return new IntLiteral(value);
    }

    public static BoolLiteral boolLit(boolean value) {
        return new BoolLiteral(value);
    }

    public static VariableExpression ident(String name, GlslType type) {
        return new VariableExpression(name, type);
    }

    public static FunctionCallExpression vec2(Expression scalar) {
        return new FunctionCallExpression("vec2", Lists.newArrayList(scalar), Vec2Type.INSTANCE);
    }

    public static FunctionCallExpression vec2(Expression x, Expression y) {
        return new FunctionCallExpression("vec2", Lists.newArrayList(x, y), Vec2Type.INSTANCE);
    }

    public static FunctionCallExpression vec3(Expression scalar) {
        return new FunctionCallExpression("vec3", Lists.newArrayList(scalar), Vec3Type.INSTANCE);
    }

    public static FunctionCallExpression vec3(Expression x, Expression y, Expression z) {
        return new FunctionCallExpression("vec3", Lists.newArrayList(x, y, z), Vec3Type.INSTANCE);
    }

    public static FunctionCallExpression vec3(Expression xy, Expression z) {
        if (xy.glslType() != Vec2Type.INSTANCE) {
            throw new GlslTypeException("vec3(xy, z): xy must be vec2");
        }
        if (z.glslType() != ScalarType.FLOAT) {
            throw new GlslTypeException("vec3(xy, z): z must be float");
        }
        return new FunctionCallExpression("vec3", Lists.newArrayList(xy, z), Vec3Type.INSTANCE);
    }

    public static FunctionCallExpression vec4(Expression x, Expression y, Expression z, Expression w) {
        return new FunctionCallExpression("vec4", Lists.newArrayList(x, y, z, w), Vec4Type.INSTANCE);
    }

    public static FunctionCallExpression vec4(Expression xyz, Expression w) {
        if (xyz.glslType() != Vec3Type.INSTANCE) {
            throw new GlslTypeException("vec4(xyz, w): xyz must be vec3");
        }
        if (w.glslType() != ScalarType.FLOAT) {
            throw new GlslTypeException("vec4(xyz, w): w must be float");
        }
        return new FunctionCallExpression("vec4", Lists.newArrayList(xyz, w), Vec4Type.INSTANCE);
    }

    public static FunctionCallExpression vec4(Expression scalar) {
        return new FunctionCallExpression("vec4", Lists.newArrayList(scalar), Vec4Type.INSTANCE);
    }

    private static BinaryExpression binary(BinaryOperator operator, Expression left, Expression right) {
        GlslType resolved = TypeResolver.resolveBinary(operator, left.glslType(), right.glslType());
        return new BinaryExpression(left, operator, right, resolved);
    }
}
