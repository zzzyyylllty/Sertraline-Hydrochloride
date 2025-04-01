# Sertraline-Hydrochloride 盐酸舍曲林 / DepazItems

**项目使用 TabooLib Start Jar 创建**

<div align="center">

[ENGLISH README HERE](README-EN.md)

Sertraline 是一个在 Minecraft Bukkit 平台下运行的物品库插件。

旨在 Taboolib 的架构下的服务器中方便，简单地利用属性插件，功能和 Kether 创建物品。

基于 <a href = "https://tabooproject.org">Taboolib 6.x</a> 和 Kotlin.

这个项目的名字来源于：
<div style="text-align: center;">游戏 <a href = "https://needystreameroverload.wiki.gg/wiki/">主播女孩重度依赖</a> 中的安神片 (Depaz Pills)</div><br>
<div style="text-align: center;">现实中的 <a href = "https://baike.baidu.com/item/%E7%9B%90%E9%85%B8%E8%88%8D%E6%9B%B2%E6%9E%97%E7%89%87/8353072">盐酸舍曲林片</a></div>
</div>

## 服务

Sertraline 是免费的，但我们不提供开发版本的构建文件。

你可通过自行构建获取开发版本，或是在 Release 下载稳定版本。

## 测试过的服务端

### Sertraline 0.1.1

* Leaf 1.20.4
* Leaf 1.21.1

## TODO
- [x] 读取文件/文件夹
- [x] 给予物品
- [ ] 物品变量
- [ ] MythicLib 属性
- [ ] Kether: 判断物品
- [ ] Kether: 构建物品
- [x] Kether: eval
- [ ] 释放技能
- [ ] Chemdah 物品识别兼容性
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

## 构建发行版本

发行版本用于正常使用, 不含 TabooLib 本体。

```
./gradlew clean build
```

## 构建 API 版本

API 版本包含 TabooLib 本体, 用于开发者使用, 但不可运行。

```
./gradlew clean taboolibBuildApi -PDeleteCode
```

> 参数 -PDeleteCode 表示移除所有逻辑代码以减少体积。