# Sertraline

项目使用 TabooLib Start Jar 创建

超天物品管理系统 (Liminal Skyline v4.0 服务)

<div align="center">

[CHINESE README](README-ZH.md) | [ENGLISH README](README-EN.md)

</div>

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

