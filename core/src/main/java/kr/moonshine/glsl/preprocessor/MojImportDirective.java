package kr.moonshine.glsl.preprocessor;

public record MojImportDirective(
        String path
) implements PreprocessorNode {
}
