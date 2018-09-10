package fr.ekito.myweatherapp.data.datasource.webservice.local

import android.app.Application
import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Read Json File from assets/json
 */
class AndroidJsonReader(val androidContext: Application) : BaseReader() {

    override fun getAllFiles(): List<String> = androidContext.assets.list("json").toList()

    override fun readJsonFile(jsonFile: String): String {
        val buf = StringBuilder()
        val json = androidContext.assets.open("json/" + jsonFile)
        BufferedReader(InputStreamReader(json, "UTF-8"))
            .use {
                val list = it.lineSequence().toList()
                buf.append(list.joinToString("\n"))
            }

        return buf.toString()
    }
}