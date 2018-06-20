package fr.ekito.myweatherapp.data.datasource.webservice.local

import java.io.File

/**
 * Java Json reader for Tests
 */
class JavaReader : BaseReader() {

    fun basePath(): String? {
        val classLoader: ClassLoader = JavaReader::class.java.classLoader
        return classLoader.getResource("json/")?.path
    }

    override fun getAllFiles(): List<String> {
        return basePath()?.let {
            val list = File(it).list()
            list.toList()
        }!!
    }

    override fun readJsonFile(jsonFile: String): String =
        File("${basePath()}/$jsonFile").readLines().joinToString(separator = "\n")
}