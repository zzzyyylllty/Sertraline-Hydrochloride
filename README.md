# Sertraline

超天物品管理系统服务 (Liminal Skyline v4.0 服务)

ChoTen Item management system service (Liminal Skyline v4.0 Service)

<div align="center">

<img alt="AVATAR" src="https://github.com/user-attachments/assets/4e876740-8759-4b0d-a42c-7a8cf9692f9d" />

<p align="center">
  <a href="https://github.com/zzzyyylllty/Sertraline-Hydrochloride/commits/main/">
    <img src="https://img.shields.io/github/last-commit/zzzyyylllty/Sertraline-Hydrochloride?logo=artstation&style=for-the-badge"/>
  </a>
  <a href="https://github.com/zzzyyylllty/Sertraline-Hydrochloride/issues">
    <img src="https://img.shields.io/github/issues/zzzyyylllty/Sertraline-Hydrochloride?style=for-the-badge&logo=slashdot"/>
  </a>
  <a href="https://github.com/zzzyyylllty/Sertraline-Hydrochloride/releases">
    <img src="https://img.shields.io/github/release/zzzyyylllty/Sertraline-Hydrochloride?style=for-the-badge&color=CC66FF&logo=ionic"/>
  </a>
</p>
<p align="center">
  <a href="https://qun.qq.com/universal-share/share?ac=1&authKey=EJ%2FBIgYus1kDxqSE5WHvAMyC7EaBqcpEFN7fBGtNFRGe6ZzjX9uECORltHB1q6Um&busi_data=eyJncm91cENvZGUiOiI4NTg4Mjc1MjMiLCJ0b2tlbiI6ImpYYlV6WEJnYXJadTNUd3pnOWU1c3ZsSFh1Z3h2UjFuc3JOb3d5VVhtRWs5bm9BKzZoZzhxckNsenhydmQyQ2oiLCJ1aW4iOiIzODI0NjM2MTk0In0%3D&data=PCCbZvFVCR3De_3tA0iMGgfjVc1xN4-yJslk2O-X4rYSvNkYvAFJymCjz9Sq6cNA1PLe_zJYmCHRRRK0FwNuHw&svctype=4&tempid=h5_group_info">
    <img src="https://img.shields.io/badge/QQ_Group-858827523-blue?style=for-the-badge"/>
  </a>
  <a href="https://discord.com/invite/VHs958jJXj">
    <img src="https://img.shields.io/discord/1272636652447338561?style=for-the-badge&label=DISCORD&color=%23ff66cc"/>
  </a>
  <a href="https://deepwiki.com/zzzyyylllty/Sertraline-Hydrochloride">
    <img src="https://img.shields.io/badge/Ask-DEEPWIKI-aqua?style=for-the-badge"/>
  </a>
</p>

**SERTRALINE Supports 1.21.4-1.21.10!**

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
* [TheFloodDragon](https://github.com/TheFloodDragon) - ratziel's author.
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

