# Sertraline

项目使用 TabooLib Start Jar 创建

超天物品管理系统服务 (Liminal Skyline v4.0 服务)

<div align="center">

[CHINESE README](README-ZH.md) | [ENGLISH README](README-EN.md)

![](https://img.shields.io/github/last-commit/zzzyyylllty/Sertraline-Hydrochloride?logo=artstation&style=for-the-badge&color=9266CC)  ![](https://img.shields.io/github/issues/zzzyyylllty/Sertraline-Hydrochloride?style=for-the-badge&logo=slashdot)  ![](https://img.shields.io/github/release/zzzyyylllty/Sertraline-Hydrochloride?style=for-the-badge&color=00C58E&logo=ionic)

</div>


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

