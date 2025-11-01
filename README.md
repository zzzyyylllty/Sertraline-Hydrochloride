# Sertraline

超天物品管理系统服务 (Liminal Skyline v4.0 服务)

ChoTen Item management system service (Liminal Skyline v4.0 Service)

<div align="center">

![](https://img.shields.io/github/last-commit/zzzyyylllty/Sertraline-Hydrochloride?logo=artstation&style=for-the-badge)  ![](https://img.shields.io/github/issues/zzzyyylllty/Sertraline-Hydrochloride?style=for-the-badge&logo=slashdot)  ![](https://img.shields.io/github/release/zzzyyylllty/Sertraline-Hydrochloride?style=for-the-badge&color=CC66FF&logo=ionic)

</div>

## Special Thanks

### Thanks for other Projects

* [TabooLib](https://github.com/TabooLib/taboolib) - plugin's framework.
* [Ratziel](https://github.com/TheFloodDragon/Ratziel-Beta) - some visual feature's reference.
* [CraftEngine](https://github.com/Xiao-MoMi/craft-engine) - some component feature's reference.
* [TrMenu](https://github.com/CoderKuo/TrMenu) - packet feature's reference.

### Thanks for other Developers

* [JhqwqMc](https://github.com/jhqwqmc) - some component feature's contributor.
* [ChengZhiMeow](https://github.com/ChengZhiMeow/) - visual feature's reference.
* [Xiao-MoMi](https://github.com/Xiao-MoMi/) - craft-engine's author, craft-engine is this plugin's reference.

## As dependency

```Gradle kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.zzzyyylllty:Sertraline-Hydrochloride:VERSION")
}
```

## Build Runtime Version

Required Java 21.

Runtime version for normal use.

Build artifact is in `plugin/build/libs` folder.

```
./gradlew clean build
```

## Build Api Version

The api version includes the TabooLib core, intended for developers' use but not runnable.

```
./gradlew clean taboolibBuildApi -PDeleteCode
```

> The parameter `-PDeleteCode` indicates the removal of all logic code to reduce size.

