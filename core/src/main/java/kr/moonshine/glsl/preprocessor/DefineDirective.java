package kr.moonshine.glsl.preprocessor;

import org.jetbrains.annotations.Nullable;

public record DefineDirective(
        String name,
        @Nullable String value
) implements PreprocessorNode {
}
