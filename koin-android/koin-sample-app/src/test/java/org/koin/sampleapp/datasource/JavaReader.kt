package org.koin.sampleapp.datasource

import org.koin.sampleapp.repository.local.BaseReader
import java.io.File

/**
 * Created by arnaud on 12/10/2017.
 */
class JavaReader : BaseReader() {

    val base_path = "koin-sample-app/src/test/resources/json"

    override fun getAllFiles(): List<String> = File(base_path).list().toList()

    override fun readJsonFile(jsonFile: String): String = File("$base_path/$jsonFile").readLines().joinToString(separator = "\n")
}