# Sertraline 盐酸舍曲林 / DepazItems

**项目使用 TabooLib Start Jar 创建**

<div align="center">

[ENGLISH README HERE](README-EN.md)

Sertraline 是一个在 Minecraft Bukkit 平台下运行的物品库插件。

旨在 Taboolib 的架构下的服务器中方便，简单地利用属性插件，功能和 Kether 创建物品。

基于 <a href = "https://tabooproject.org">Taboolib 6.x</a> 和 Kotlin.

**原生版本 1.20.4 & Paper**

**测试过的版本 Paper 1.20.4-1.21.4**

**需要 Java 版本 >= 21 & Paper**

这个项目的名字来源于：
<div style="text-align: center;">游戏 <a href = "https://needystreameroverload.wiki.gg/wiki/">主播女孩重度依赖</a> 中的安神片 (Depaz Pills)</div><br>
<div style="text-align: center;">现实中的 <a href = "https://baike.baidu.com/item/%E7%9B%90%E9%85%B8%E8%88%8D%E6%9B%B2%E6%9E%97%E7%89%87/8353072">盐酸舍曲林片</a></div>
</div>

## 服务

Sertraline 是免费的，但我们不提供非稳定版本的构建文件。

你可通过自行构建获取不稳定版本，或是在 Release 下载稳定版本。

## 赞助

[爱发电](https://afdian.com/a/liminalskyline)

## TODO
- [x] 读取文件/文件夹
- [x] 给予物品
- [x] 物品变量
- [x] MythicLib 属性
- [ ] 自动属性更新
- [ ] Kether: 判断物品
- [ ] Kether: 构建物品
- [x] Kether: 消耗物品
- [x] Kether: eval
- [ ] 释放技能
- [x] Chemdah 物品识别兼容性
...

## 友链   
- [TabooLib 非官方文档](https://taboolib.feishu.cn/)
- [Zaphkiel](https://github.com/TabooLib/zaphkiel)
- [MythicLib](https://www.spigotmc.org/resources/mmolib-mythiclib.90306/)
- [Leaf Server](https://github.com/Winds-Studio/Leaf)
- [精神支柱](https://needystreameroverload.wiki.gg/wiki/Ame-chan)

## 贡献者

<a href="https://github.com/zzzyyylllty/Sertraline-Hydrochloride/graphs/contributors">
  <img src="https://stg.contrib.rocks/image?repo=zzzyyylllty/Sertraline-Hydrochloride" />
</a>

## 添加到依赖

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.zzzyyylllty:Sertraline-Hydrochloride:VERSION")
}
```

## 构建发行版本

需要 Java 21.

发行版本用于正常使用, 不含 TabooLib 本体。

```
./gradlew clean build
```

## 构建开发版本

开发版本包含 TabooLib 本体, 用于开发者使用, 但不可运行。

```
./gradlew clean taboolibBuildApi -PDeleteCode
```

> 参数 -PDeleteCode 表示移除所有逻辑代码以减少体积。
