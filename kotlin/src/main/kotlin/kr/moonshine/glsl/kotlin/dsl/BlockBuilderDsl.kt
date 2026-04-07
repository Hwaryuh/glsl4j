package kr.moonshine.glsl.kotlin.dsl

import kr.moonshine.glsl.ast.stmt.Block
import kr.moonshine.glsl.ast.stmt.SwitchCase
import kr.moonshine.glsl.builder.BlockBuilder
import kr.moonshine.glsl.builder.SwitchCaseBuilder

fun block(init: BlockBuilder.() -> Unit): Block = BlockBuilder.create().apply(init).build()

fun switchCase(
    id: Int,
    init: SwitchCaseBuilder.() -> Unit,
): SwitchCase = SwitchCaseBuilder.of(id).apply(init).build()
