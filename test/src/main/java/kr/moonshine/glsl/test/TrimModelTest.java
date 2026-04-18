package kr.moonshine.glsl.test;

import kr.moonshine.glsl.ast.expr.ArrayConstructorExpression;
import kr.moonshine.glsl.ast.expr.Expression;
import kr.moonshine.glsl.ast.expr.FunctionCallExpression;
import kr.moonshine.glsl.ast.expr.MacroCallExpression;
import kr.moonshine.glsl.ast.stmt.AssignmentOperator;
import kr.moonshine.glsl.ast.stmt.SwitchCase;
import kr.moonshine.glsl.builder.SwitchCaseBuilder;
import kr.moonshine.glsl.emit.EmitMode;
import kr.moonshine.glsl.emit.GlslSnippetEmitter;
import kr.moonshine.glsl.emit.GlslWriter;
import kr.moonshine.glsl.type.ArrayType;
import kr.moonshine.glsl.type.Mat3Type;
import kr.moonshine.glsl.type.ScalarType;
import kr.moonshine.glsl.type.Vec2Type;
import kr.moonshine.glsl.type.Vec3Type;
import kr.moonshine.glsl.type.Vec4Type;

import java.nio.file.Path;
import java.util.List;

import static kr.moonshine.glsl.ast.expr.Expressions.*;

public class TrimModelTest {

    public static void main(String[] args) {
        var switchCase = buildCase();
        var source = new GlslSnippetEmitter(EmitMode.PRETTY).emitSwitchCase(switchCase);
        GlslWriter.write(source, Path.of("output"), "test_trim_model.glsl");
    }

    private static SwitchCase buildCase() {
        var arraySize = 3;
        var floatArrayType = new ArrayType(ScalarType.FLOAT, arraySize);
        var vec3ArrayType = new ArrayType(Vec3Type.INSTANCE, arraySize);
        var intArrayType = new ArrayType(ScalarType.INT, arraySize);

        // 외부 스코프 변수
        var modelSize = ident("modelSize", ScalarType.FLOAT);
        var stp = ident("stp", Vec2Type.INSTANCE);

        // float t = ANIMATION_TIME(1.0)
        var animTime = new MacroCallExpression(
                "ANIMATION_TIME",
                List.of(floatLit(1.0f)),
                ScalarType.FLOAT);

        // RAD(expr) = expr * 0.017453292
        var rad = 0.017453292f;

        var rotTimes = new ArrayConstructorExpression(
                floatArrayType,
                List.of(floatLit(0.0f), floatLit(0.5f), floatLit(1.0f)));

        var rotValues = new ArrayConstructorExpression(
                vec3ArrayType,
                List.of(
                        vec3(floatLit(0.0f), floatLit(-45.0f), floatLit(0.0f)),
                        vec3(floatLit(0.0f), floatLit(45.0f), floatLit(0.0f)),
                        vec3(floatLit(0.0f), floatLit(-45.0f), floatLit(0.0f))));

        // CATMULLROM = 1
        var rotInterpolationTypes = new ArrayConstructorExpression(
                intArrayType,
                List.of(intLit(1), intLit(1), intLit(1)));

        var tVar = ident("t", ScalarType.FLOAT);
        var rotTimesVar = ident("rotTimes", floatArrayType);
        var rotValuesVar = ident("rotValues", vec3ArrayType);
        var rotInterpVar = ident("rotInterpolationTypes", intArrayType);
        var rotVar = ident("rot", Vec3Type.INSTANCE);
        var bone1RotVar = ident("bone1Rot", Mat3Type.INSTANCE);
        var bone1PosVar = ident("bone1Pos", Vec3Type.INSTANCE);
        var bone1ScaleVar = ident("bone1Scale", Vec3Type.INSTANCE);
        var bone1PivotVar = ident("bone1Pivot", Vec3Type.INSTANCE);

        // Rotate3(RAD(rot.x), X) * Rotate3(RAD(rot.y), Y) * Rotate3(RAD(rot.z), Z)
        var rotX = swizzle(rotVar, "x", ScalarType.FLOAT);
        var rotY = swizzle(rotVar, "y", ScalarType.FLOAT);
        var rotZ = swizzle(rotVar, "z", ScalarType.FLOAT);

        var bone1Rot = mul(
                mul(
                        call("Rotate3", Mat3Type.INSTANCE, mul(rotX, floatLit(rad)), intLit(0)),
                        call("Rotate3", Mat3Type.INSTANCE, mul(rotY, floatLit(rad)), intLit(1))),
                call("Rotate3", Mat3Type.INSTANCE, mul(rotZ, floatLit(rad)), intLit(2)));

        // ADD_BOX_ROTATE 인자들
        // vec4(stp + vec2(x, y), vec2(w, h))
        var uvFace = uvRect(stp, -40, -16, 8, -8);
        var uvTop = uvRect(stp, -24, -32, 8, -8);
        var uvBot = uvRect(stp, -40, -40, 8, 8);
        var uvFrt = uvRect(stp, -40, -32, 8, 8);
        var uvBck = uvRect(stp, -32, -40, 8, 8);
        var uvSid = uvRect(stp, -32, -32, 8, 8);

        return SwitchCaseBuilder.of(8)
                .compoundAssign(modelSize,
                        AssignmentOperator.DIV_ASSIGN,
                        intLit(14))
                .local(ScalarType.FLOAT, "t", animTime)
                .constLocal(floatArrayType, "rotTimes", rotTimes)
                .constLocal(vec3ArrayType, "rotValues", rotValues)
                .constLocal(intArrayType, "rotInterpolationTypes", rotInterpolationTypes)
                .local(Vec3Type.INSTANCE, "bone1Pos", vec3(intLit(0), intLit(0), intLit(4)))
                .local(Vec3Type.INSTANCE, "bone1Scale", vec3(intLit(8), intLit(8), intLit(8)))
                .local(Vec3Type.INSTANCE, "rot",
                        call("interpolate", Vec3Type.INSTANCE,
                                rotTimesVar, rotValuesVar, rotInterpVar, tVar))
                .local(Mat3Type.INSTANCE, "bone1Rot", bone1Rot)
                .local(Vec3Type.INSTANCE, "bone1Pivot", vec3(intLit(0), intLit(0), intLit(-4)))
                .macroCall("ADD_BOX_ROTATE",
                        bone1PosVar, bone1ScaleVar,
                        bone1RotVar, bone1PivotVar,
                        uvFace, uvTop, uvBot, uvFrt, uvBck, uvSid)
                .build();
    }

    // vec4(stp + vec2(offsetX, offsetY), vec2(sizeW, sizeH))
    private static FunctionCallExpression uvRect(
            Expression stp,
            int ox, int oy, int sw, int sh) {
        return call("vec4", Vec4Type.INSTANCE,
                add(stp, vec2(intLit(ox), intLit(oy))),
                vec2(intLit(sw), intLit(sh)));
    }
}
