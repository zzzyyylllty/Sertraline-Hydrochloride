package io.github.zzzyyylllty.sertraline.compat

/**
 * i18n log bridge. The spigot source set does not depend on the common module (to avoid circular dependencies),
 * so it cannot use common's warningL directly; common registers a translation handler at startup and the platform
 * layer (spigot source set) outputs i18n warning logs through this bridge.
 */
object CompatLog {

    @Volatile
    private var handler: ((String, Array<out Any>) -> Unit)? = null

    /** Registers the i18n warning handler (called at common startup, mapped to warningL) */
    fun register(handler: (String, Array<out Any>) -> Unit) {
        this.handler = handler
    }

    /** Outputs an i18n warning; stays silent when unregistered (these warnings are all platform fallback notices and don't affect functionality) */
    fun warning(node: String, vararg args: Any) {
        handler?.invoke(node, args)
    }
}
