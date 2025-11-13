package io.github.zzzyyylllty.sertraline.function.fluxon

import org.tabooproject.fluxon.parser.ParseResult
import org.tabooproject.fluxon.parser.expression.literal.Identifier
import org.tabooproject.fluxon.parser.expression.literal.Literal
import org.tabooproject.fluxon.parser.statement.ExpressionStatement
import org.tabooproject.fluxon.runtime.Environment
import org.tabooproject.fluxon.runtime.FluxonRuntime

open class ParseScript(val blocks: List<ParseResult>?) {

    /**
     * 是否为明文（只有一个 Identifier 或者 StringLiteral）
     */
    val isPlain = if (blocks?.size == 1) {
        val result = blocks[0]
        if (result is ExpressionStatement) {
            result.expression is Identifier || result.expression is Literal
        } else false
    } else false

    /**
     * 执行脚本
     *
     * @param env 脚本执行环境
     */
    open fun invoke(env: Environment.() -> Unit = {}): Any? {
        if (blocks == null) return null
        // 如果是明文，则跳过脚本环境直接取值
        if (isPlain) {
            val result = blocks[0] as ExpressionStatement
            return when (val expr = result.expression) {
                is Identifier -> expr.value
                is Literal -> expr.sourceValue
                else -> null
            }
        }
        return FluxonShell.invoke(blocks, FluxonRuntime.getInstance().newEnvironment().also(env))
    }
}