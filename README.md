# Sertraline-Hydrochloride

> 尚未完工

## Finding English README?

[English](README_EN.md)


## TODO

- [ ] 插件兼容 - 兼容其他与MMOItems等插件兼容的任何插件
  - [ ] MMOItems
- [ ] 基本物品构建 - 名称，lore，自定义NBT...
- [ ] 物品数据/常量/变量储存 - 可以用在任何地方，包括权重分组，数学公式，额定值等!
  - [ ] 物品数据 FIXED
  - [ ] 物品常量 VAL
  - [ ] 物品变量 VAR
- [ ] 主要属性功能 - 提供可能出现的属性，可修改的自动lore格式等
  - [ ] MYTHIC_LIB
  - [ ] ATTRIBUTE_PLUS
  - [ ] MMOITEMS
  - [ ] MYTHIC_MOBS
  - [ ] SX_ATTRIBUTE_2.0
  - [ ] SX_ATTRIBUTE_3.0
- [ ] 动作 - 任何动作都支持shift检测，ari keys自定义按键绑定
  - [ ] 普通动作
  - [ ] Ari keys
- [ ] 自动更新物品
- [ ] 脚本
  - [ ] Kether
  - [ ] JavaScript
- [ ] MMSkill 支持
- [ ] 原生概率执行命令
- [ ] 玩家数据
  - [ ] 数据库
- [ ] 多版本兼容性(大概没戏)
  - [ ] 1.8
  - [ ] 1.12
  - [ ] 1.13
  - [ ] 1.20
  - [ ] 1.20.6
- [x] 基底物品支持
  - [x] MMOItems
  - [x] MythicMobs
  - [x] Zaphkiel
  - [x] EcoItems
  - [x] NeigeItems
  - [x] 字符串转换物品
  - [x] SX-Item
  - [x] ItemsAdder
  - [x] MagicCosmetics
  - [x] Oxaren

...

## 构建发行版本 / 构建在游戏内运行的版本

发行版本用于正常使用, 不含 TabooLib 本体。

```
./gradlew build
```

## 构建开发版本

开发版本包含 TabooLib 本体, 用于开发者使用, 但不可运行。

```
./gradlew taboolibBuildApi -PDeleteCode
```

> 参数 -PDeleteCode 表示移除所有逻辑代码以减少体积。
