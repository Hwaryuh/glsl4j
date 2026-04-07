package kr.moonshine.glsl.validate;

import kr.moonshine.glsl.ast.expr.BinaryOperator;
import kr.moonshine.glsl.type.GlslType;
import kr.moonshine.glsl.type.MatrixType;
import kr.moonshine.glsl.type.ScalarType;
import kr.moonshine.glsl.type.Vec2Type;
import kr.moonshine.glsl.type.Vec3Type;
import kr.moonshine.glsl.type.Vec4Type;
import kr.moonshine.glsl.type.VectorType;

public final class TypeResolver {

    private TypeResolver() {
    }

    public static GlslType resolveBinary(BinaryOperator operator, GlslType left, GlslType right) {
        return switch (operator) {
            case ADD, SUB, DIV, MOD -> resolveArithmetic(left, right);
            case MUL -> resolveMul(left, right);
            case EQ, NEQ, LT, GT, LTE, GTE -> resolveComparison(left, right);
            case AND, OR -> resolveLogical(left, right);
            case BIT_AND, BIT_OR, BIT_XOR, SHL, SHR -> resolveBitwise(left, right);
        };
    }

    private static GlslType resolveArithmetic(GlslType left, GlslType right) {
        if (left.equals(right)) return left;
        if (left == ScalarType.FLOAT && right instanceof VectorType) return right;
        if (right == ScalarType.FLOAT && left instanceof VectorType) return left;
        if (left == ScalarType.FLOAT && right instanceof MatrixType) return right;
        if (right == ScalarType.FLOAT && left instanceof MatrixType) return left;
        if (left == ScalarType.INT && right instanceof VectorType v && v.elementType() == ScalarType.INT) return right;
        if (right == ScalarType.INT && left instanceof VectorType v && v.elementType() == ScalarType.INT) return left;
        throw new GlslTypeException("Incompatible types: " + left.glslName() + ", " + right.glslName());
    }

    private static GlslType resolveMul(GlslType left, GlslType right) {
        if (left.equals(right)) return left;
        if (left == ScalarType.FLOAT && right instanceof VectorType) return right;
        if (right == ScalarType.FLOAT && left instanceof VectorType) return left;
        if (left == ScalarType.FLOAT && right instanceof MatrixType) return right;
        if (right == ScalarType.FLOAT && left instanceof MatrixType) return left;
        if (left == ScalarType.INT && right instanceof VectorType v && v.elementType() == ScalarType.INT) return right;
        if (right == ScalarType.INT && left instanceof VectorType v && v.elementType() == ScalarType.INT) return left;
        if (left instanceof MatrixType m && right instanceof VectorType v) {
            if (m.columns() == v.dimension()) return resolveMatVecMul(m);
            throw new GlslTypeException("Matrix columns must match vector dimension");
        }
        throw new GlslTypeException("Incompatible types: " + left.glslName() + ", " + right.glslName());
    }

    private static GlslType resolveMatVecMul(MatrixType mat) {
        return switch (mat.rows()) {
            case 2 -> Vec2Type.INSTANCE;
            case 3 -> Vec3Type.INSTANCE;
            case 4 -> Vec4Type.INSTANCE;
            default -> throw new GlslTypeException("Unexpected matrix rows: " + mat.rows());
        };
    }

    private static GlslType resolveComparison(GlslType left, GlslType right) {
        if (!left.equals(right)) {
            throw new GlslTypeException("Comparison requires same types: " + left.glslName() + ", " + right.glslName());
        }
        return ScalarType.BOOL;
    }

    private static GlslType resolveLogical(GlslType left, GlslType right) {
        if (left != ScalarType.BOOL || right != ScalarType.BOOL) {
            throw new GlslTypeException("Logical operators require bool operands");
        }
        return ScalarType.BOOL;
    }

    private static GlslType resolveBitwise(GlslType left, GlslType right) {
        if (left != ScalarType.INT || right != ScalarType.INT) {
            throw new GlslTypeException("Bitwise operators require int operands");
        }
        return ScalarType.INT;
    }
}
