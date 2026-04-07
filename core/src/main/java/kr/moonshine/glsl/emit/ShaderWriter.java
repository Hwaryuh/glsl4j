package kr.moonshine.glsl.emit;

import kr.moonshine.glsl.ast.ShaderUnit;
import kr.moonshine.glsl.dialect.GlslDialect;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ShaderWriter {

    private final GlslEmitter emitter;

    public ShaderWriter(GlslDialect dialect, EmitMode mode) {
        this.emitter = new GlslEmitter(dialect, mode);
    }

    public Path write(ShaderUnit unit, Path outputDir) {
        var fileName = unit.name() + unit.extension();
        var outputPath = outputDir.resolve(fileName);
        var source = emitter.emit(unit);
        try {
            Files.createDirectories(outputDir);
            Files.writeString(outputPath, source, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return outputPath;
    }
}
