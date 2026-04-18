package kr.moonshine.glsl.test;

import kr.moonshine.glsl.ast.ShaderSnippet;
import kr.moonshine.glsl.ast.expr.Expression;
import kr.moonshine.glsl.ast.stmt.IfStatement;
import kr.moonshine.glsl.builder.BlockBuilder;
import kr.moonshine.glsl.emit.EmitMode;
import kr.moonshine.glsl.emit.GlslSnippetEmitter;
import kr.moonshine.glsl.emit.GlslWriter;
import kr.moonshine.glsl.type.ScalarType;
import kr.moonshine.glsl.type.Vec4Type;

import java.nio.file.Path;
import java.util.List;

import static kr.moonshine.glsl.ast.expr.Expressions.*;

public class TrimDetectionTest {

    public static void main(String[] args) {
        var snippet = buildSnippet();
        var source = new GlslSnippetEmitter(EmitMode.PRETTY).emit(snippet);
        GlslWriter.write(source, Path.of("output"), "test_trim_detection.glsl");
    }

    private static ShaderSnippet buildSnippet() {
        // 외부 스코프 변수
        var armorMarkerNormal = ident("armorMarkerNormal", Vec4Type.INSTANCE);
        var armorMarkerMirroredX = ident("armorMarkerMirroredX", Vec4Type.INSTANCE);
        var armorMarkerMirroredY = ident("armorMarkerMirroredY", Vec4Type.INSTANCE);
        var faceID = ident("faceID", ScalarType.INT);
        var cem = ident("cem", ScalarType.INT);
        var cemReverse = ident("cem_reverse", ScalarType.INT);
        var cemSize = ident("cem_size", ScalarType.FLOAT);
        var armorTestColorIn = ident("armorTestColorIn", Vec4Type.INSTANCE);

        // 인라인 상수
        var markerColor = call("vec4", Vec4Type.INSTANCE, floatLit(255f), floatLit(0f), floatLit(1f), floatLit(252f));
        var markerColorA = floatLit(252f);
        var face1 = intLit(15);
        var face2 = intLit(17);

        // max(max(armorMarkerNormal.a, armorMarkerMirroredX.a), armorMarkerMirroredY.a)
        Expression maxAlpha = call("max", ScalarType.FLOAT,
                call("max", ScalarType.FLOAT,
                        swizzle(armorMarkerNormal, "a", ScalarType.FLOAT),
                        swizzle(armorMarkerMirroredX, "a", ScalarType.FLOAT)),
                swizzle(armorMarkerMirroredY, "a", ScalarType.FLOAT));

        // 외부 if 조건
        var outerCondition = and(
                eq(maxAlpha, markerColorA),
                or(eq(faceID, face1), eq(faceID, face2)));

        // else if 체인
        var elseIfMirroredY = new IfStatement(
                eq(armorMarkerMirroredY, markerColor),
                BlockBuilder.create()
                        .assign(armorTestColorIn, armorMarkerMirroredY)
                        .build(),
                null);

        var elseIfMirroredX = new IfStatement(
                eq(armorMarkerMirroredX, markerColor),
                BlockBuilder.create()
                        .assign(armorTestColorIn, armorMarkerMirroredX)
                        .build(),
                elseIfMirroredY);

        var innerColorCheck = new IfStatement(
                eq(armorMarkerNormal, markerColor),
                BlockBuilder.create()
                        .assign(armorTestColorIn, armorMarkerNormal)
                        .build(),
                elseIfMirroredX);

        // if (armorTestColorIn == MARKER_COLOR) { ... } else { gl_Position = vec4(0); }
        var testColorCheck = new IfStatement(
                eq(armorTestColorIn, markerColor),
                BlockBuilder.create()
                        .ifStmt(eq(faceID, face1),
                                BlockBuilder.create()
                                        .assign(cem, intLit(8))
                                        .assign(cemReverse, intLit(1))
                                        .build(),
                                BlockBuilder.create()
                                        .assign(cem, intLit(9))
                                        .assign(cemReverse, intLit(1))
                                        .build())
                        .assign(cemSize, floatLit(2.0f))
                        .build(),
                BlockBuilder.create()
                        .assign(ident("gl_Position", Vec4Type.INSTANCE),
                                call("vec4", Vec4Type.INSTANCE, intLit(0)))
                        .build());

        // 외부 if body
        var outerBody = BlockBuilder.create()
                .local(Vec4Type.INSTANCE, "armorTestColorIn", call("vec4", Vec4Type.INSTANCE, intLit(-1)))
                .stmt(innerColorCheck)
                .stmt(testColorCheck)
                .build();

        return new ShaderSnippet(List.of(
                new IfStatement(outerCondition, outerBody, null)
        ));
    }
}
