package io.github.zzzyyylllty.sertraline.function.fluxon

import org.tabooproject.fluxon.parser.ParsedScript
import org.tabooproject.fluxon.parser.expression.literal.Identifier
import org.tabooproject.fluxon.parser.expression.literal.Literal
import org.tabooproject.fluxon.parser.statement.ExpressionStatement
import org.tabooproject.fluxon.runtime.Environment
import org.tabooproject.fluxon.runtime.FluxonRuntime

open class ParseScript(val blocks: ParsedScript?) {

    /**
     * 执行脚本
     *
     * @param env 脚本执行环境
     */
    open fun invoke(env: Environment.() -> Unit = {}): Any? {
        if (blocks == null) return null
        return FluxonShell.invoke(blocks, FluxonRuntime.getInstance().newEnvironment().also(env))
    }
}