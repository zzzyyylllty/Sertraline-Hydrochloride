# Sertraline / DepazItems

**Project created using TabooLib Start Jar**

<div align="center">

[ENGLISH README HERE](README-EN.md)

Sertraline is an item library plugin that runs on the Minecraft Bukkit platform.

Intended to facilitate and simplify the creation of items using attribute plugins, functionalities, and Kether in servers under the Taboolib architecture.

Based on <a href = "https://tabooproject.org">Taboolib 6.x</a> + Kotlin.

**NATIVE 1.20.4**

**TESTED 1.20.4-1.21.4**

**REQUIRED JAVA 21**

Project Name From：
<div style="text-align: center;">Game <a href = "https://needystreameroverload.wiki.gg/wiki/">Needy Girl Overdose</a>'s Depaz Pills</div><br>
<div style="text-align: center;"><a href = "https://baike.baidu.com/item/%E7%9B%90%E9%85%B8%E8%88%8D%E6%9B%B2%E6%9E%97%E7%89%87/8353072">Sertraline - Hydrochloride</a></div>
</div>

## Services

Sertraline is free, but we do not provide build files for unstable versions.

You can obtain unstable versions by building them yourself, or download stable versions from Release.

## TODO
- [x] Read files/folders
- [x] Give items
- [x] Item variable
- [x] MythicLib attribute
- [ ] Automatic attribute update
- [ ] Kether:  Determine the item
- [ ] Kether:  Building items
- [x] Kether:  Consumable items
- [x] Kether: eval 
- [ ] Release skills
- [ ] Chemdah item recognition compatibility
...

## Friendly Links
- [TabooLib Docs](https://taboolib.feishu.cn/)
- [Zaphkiel](https://github.com/TabooLib/zaphkiel)
- [MythicLib](https://www.spigotmc.org/resources/mmolib-mythiclib.90306/)
- [Leaf Server](https://github.com/Winds-Studio/Leaf)

## Author

<a href="https://github.com/zzzyyylllty/Sertraline-Hydrochloride/graphs/contributors">
  <img src="https://stg.contrib.rocks/image?repo=zzzyyylllty/Sertraline-Hydrochloride" />
</a>

## Build release version

Required Java 17.

The release version is intended for normal use and does not include the TabooLib ontology.

```
./gradlew clean build
```

## Build development version

The development version includes the TabooLib ontology for developers to use, but it is not runnable.

```
./gradlew clean taboolibBuildApi -PDeleteCode
```

> 参数 -PDeleteCode 表示移除所有逻辑代码以减少体积。