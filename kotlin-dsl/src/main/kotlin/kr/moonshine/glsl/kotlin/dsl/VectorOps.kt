package kr.moonshine.glsl.kotlin.dsl

import kr.moonshine.glsl.ast.expr.BinaryExpression
import kr.moonshine.glsl.ast.expr.Expression
import kr.moonshine.glsl.ast.expr.Expressions
import kr.moonshine.glsl.ast.expr.FunctionCallExpression
import kr.moonshine.glsl.ast.expr.SwizzleExpression
import kr.moonshine.glsl.ast.expr.UnaryExpression
import kr.moonshine.glsl.type.ScalarType
import kr.moonshine.glsl.type.Vec2Type
import kr.moonshine.glsl.type.Vec3Type
import kr.moonshine.glsl.type.Vec4Type

operator fun Expression.plus(other: Expression): BinaryExpression = Expressions.add(this, other)

operator fun Expression.minus(other: Expression): BinaryExpression = Expressions.sub(this, other)

operator fun Expression.times(other: Expression): BinaryExpression = Expressions.mul(this, other)

operator fun Expression.div(other: Expression): BinaryExpression = Expressions.div(this, other)

operator fun Expression.rem(other: Expression): BinaryExpression = Expressions.mod(this, other)

operator fun Expression.unaryMinus(): UnaryExpression = Expressions.negate(this)

operator fun Expression.not(): UnaryExpression = Expressions.not(this)

val Expression.xy get(): SwizzleExpression = Expressions.swizzle(this, "xy", Vec2Type.INSTANCE)
val Expression.xyz get(): SwizzleExpression = Expressions.swizzle(this, "xyz", Vec3Type.INSTANCE)
val Expression.xyzw get(): SwizzleExpression = Expressions.swizzle(this, "xyzw", Vec4Type.INSTANCE)
val Expression.x get(): SwizzleExpression = Expressions.swizzle(this, "x", ScalarType.FLOAT)
val Expression.y get(): SwizzleExpression = Expressions.swizzle(this, "y", ScalarType.FLOAT)
val Expression.z get(): SwizzleExpression = Expressions.swizzle(this, "z", ScalarType.FLOAT)
val Expression.w get(): SwizzleExpression = Expressions.swizzle(this, "w", ScalarType.FLOAT)

fun Expression.dot(other: Expression): FunctionCallExpression = Expressions.call("dot", ScalarType.FLOAT, this, other)

fun Expression.normalize(): FunctionCallExpression = Expressions.call("normalize", this.glslType(), this)

fun Expression.length(): FunctionCallExpression = Expressions.call("length", ScalarType.FLOAT, this)
