package kr.moonshine.glsl.emit.obfuscation;

import kr.moonshine.glsl.ast.ShaderUnit;
import kr.moonshine.glsl.emit.ObfuscationFeature;
import kr.moonshine.glsl.emit.ObfuscationPass;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class ObfuscationPipeline {

    private ObfuscationPipeline() {
    }

    public static ShaderUnit apply(
            ShaderUnit unit,
            Set<ObfuscationFeature> features,
            Set<String> externalScope
    ) {
        if (features.isEmpty()) return unit;
        List<ObfuscationPass> passes = List.of(
                new IdentifierObfuscationPass(externalScope),
                new LiteralObfuscationPass()
        );
        var result = unit;
        for (var pass : passes) {
            result = pass.apply(result, features);
        }
        return result;
    }

    public static ShaderUnit apply(ShaderUnit unit, Set<ObfuscationFeature> features) {
        return apply(unit, features, Set.of());
    }

    public static ShaderUnit applyAll(ShaderUnit unit, Set<String> externalScope) {
        return apply(unit, EnumSet.allOf(ObfuscationFeature.class), externalScope);
    }

    public static ShaderUnit applyAll(ShaderUnit unit) {
        return apply(unit, EnumSet.allOf(ObfuscationFeature.class), Set.of());
    }
}
