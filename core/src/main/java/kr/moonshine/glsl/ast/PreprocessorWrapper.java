package kr.moonshine.glsl.ast;

import kr.moonshine.glsl.preprocessor.PreprocessorNode;

public record PreprocessorWrapper(
        PreprocessorNode node
) implements TopLevelNode {
}
