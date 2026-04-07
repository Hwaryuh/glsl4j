package kr.moonshine.glsl.preprocessor;

import java.util.List;

public record IfndefDirective(
        String name,
        List<PreprocessorNode> thenNodes,
        List<PreprocessorNode> elseNodes
) implements PreprocessorNode {
}
