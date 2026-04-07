package kr.moonshine.glsl.ast;

import kr.moonshine.glsl.GlslVersion;
import kr.moonshine.glsl.ShaderStage;

import java.util.List;

public record ShaderUnit(
        String name,
        String extension,
        GlslVersion version,
        ShaderStage stage,
        List<TopLevelNode> nodes
) {
}
