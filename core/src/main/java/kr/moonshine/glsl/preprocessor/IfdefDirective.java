package kr.moonshine.glsl.preprocessor;

import java.util.List;

public record IfdefDirective(
        String name,
        List<PreprocessorNode> thenNodes,
        List<PreprocessorNode> elseNodes
) implements PreprocessorNode {
}
