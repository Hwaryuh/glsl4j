package kr.moonshine.glsl.preprocessor;

public sealed interface PreprocessorNode
        permits DefineDirective, UndefDirective, IfdefDirective,
        IfndefDirective, IncludeDirective, MojImportDirective {
}
