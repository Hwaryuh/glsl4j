package kr.moonshine.glsl.emit;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GlslWriter {

    private GlslWriter() {
    }

    public static Path write(String source, Path outputDir, String fileName) {
        var outputPath = outputDir.resolve(fileName);
        try {
            Files.createDirectories(outputDir);
            Files.writeString(outputPath, source, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return outputPath;
    }
}
