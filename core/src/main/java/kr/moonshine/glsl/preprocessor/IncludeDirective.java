package kr.moonshine.glsl.preprocessor;

public record IncludeDirective(
        String path
) implements PreprocessorNode {
}
