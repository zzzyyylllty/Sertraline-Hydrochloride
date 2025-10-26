package io.github.zzzyyylllty.sertraline.config

fun configReader() {

    fun readYaml(filePath: String) {
        val yaml = Yaml()
        val inputStream = FileInputStream(filePath)
        val data = yaml.load<Map<String, Any>>(inputStream)

        println(data)
    }
}
