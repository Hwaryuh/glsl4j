# glsl4j

A Java library for programmatically building and emitting GLSL shaders, designed for Minecraft shader developers.

## Features

- Type-safe GLSL AST construction
- Support for Mojang Core Shader and vanilla GLSL 150/330 dialects
- Shader obfuscation (identifier renaming, literal splitting)
- Kotlin DSL support

## Requirements

- Java 21+

## Modules

- `core` — AST, emitter, validator, obfuscation
- `kotlin-dsl` — Kotlin DSL extensions

## Usage

```java
ShaderUnit unit = new ShaderUnit(
        "rendertype_entity_cutout",
        ".vsh",
        GlslVersion.V330,
        ShaderStage.VERTEX,
        List.of(...)
);

String source = new GlslEmitter(MojangCoreShaderDialect.INSTANCE, EmitMode.PRETTY).emit(unit);
```
