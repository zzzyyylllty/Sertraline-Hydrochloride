package io.github.zzzyyylllty.sertraline.reflect

class ReflectTargets {

    val componentRegistry by lazy { getBuiltInRegistries() }
    val componentCodec by lazy { getCodecMethod() }
}