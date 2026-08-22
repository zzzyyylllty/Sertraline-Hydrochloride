package io.github.zzzyyylllty.sertraline.config

import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

/**
 * 自定义事件注册配置（custom-events.yml）
 *
 * 与 config.yml 的 custom-events 段分离为独立配置文件，
 * 键名为触发名，值为事件类 / 优先级 / accept-cancelled / item-source。
 * 参考 [io.github.zzzyyylllty.sertraline.manager.ManagerConfig] 的加载方式。
 */
object CustomEventsConfig {

    @Config("custom-events.yml", migrate = true)
    lateinit var config: Configuration
        private set
}
