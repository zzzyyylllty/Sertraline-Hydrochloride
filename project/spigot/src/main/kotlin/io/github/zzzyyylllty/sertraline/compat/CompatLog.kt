package io.github.zzzyyylllty.sertraline.compat

/**
 * i18n 日志桥。spigot 源集不依赖 common 模块（避免循环依赖），无法直接使用 common 的 warningL；
 * 由 common 在启动时注册翻译处理器，平台层（spigot 源集）通过本桥输出带 i18n 的警告日志。
 */
object CompatLog {

    @Volatile
    private var handler: ((String, Array<out Any>) -> Unit)? = null

    /** 注册 i18n 警告处理器（common 启动时调用，映射到 warningL） */
    fun register(handler: (String, Array<out Any>) -> Unit) {
        this.handler = handler
    }

    /** 输出带 i18n 的警告；未注册时静默（此类警告均为平台降级提示，不影响功能） */
    fun warning(node: String, vararg args: Any) {
        handler?.invoke(node, args)
    }
}
