//extra["koin_version"] = "2.1.0-alpha-8"
//extra["kotlin_version"] = "1.3.61"
//extra["coroutines_version"] = "1.3.0"
//extra["dokka_version"] = "0.9.16"
//extra["bintray_version"] = "1.8.4"
extra["ktor_version"] = "1.2.6"
//extra["junit_version"] = "4.12"
//extra["mockito_version"] = "2.21.0"
extra["asciidoctor_version"] = "1.5.3"
extra["asciidoctor_pdf_version"] = "1.5.0-alpha.15"
extra["android_maven_publish"] = "3.6.2"

// Made to support `by extra`
// example: `val kotlinVersion: String by extra`
extra.properties.filterKeys {
    it.contains("_")
}.map { (key, value) ->
    val keyCapitalize = key.split("_").joinToString(separator = "") { it.capitalize() }.decapitalize()
    logger.info("$key > $keyCapitalize")
    keyCapitalize to value
}.filterNot {
    extra.has(it.first)
}.forEach { (key, value) ->
    extra[key] = value
}
