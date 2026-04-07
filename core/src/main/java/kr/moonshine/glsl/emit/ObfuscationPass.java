package kr.moonshine.glsl.emit;

import kr.moonshine.glsl.ast.ShaderUnit;

import java.util.Set;

public interface ObfuscationPass {

    ShaderUnit apply(ShaderUnit unit, Set<ObfuscationFeature> features);
}
