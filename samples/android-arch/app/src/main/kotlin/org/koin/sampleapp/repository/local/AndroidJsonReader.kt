package org.koin.sampleapp.repository.local

import android.app.Application
import java.io.BufferedReader
import java.io.InputStreamReader

class AndroidJsonReader(val application: Application) : BaseReader() {

    override fun getAllFiles(): List<String> = application.assets.list("json").toList()

    override fun readJsonFile(jsonFile: String): String {
        val buf = StringBuilder()
        val json = application.assets.open("json/" + jsonFile)
        BufferedReader(InputStreamReader(json, "UTF-8")).use { br ->
            buf.append(br.lineSequence().joinToString(separator = "\n"))
        }
        return buf.toString()
    }
}