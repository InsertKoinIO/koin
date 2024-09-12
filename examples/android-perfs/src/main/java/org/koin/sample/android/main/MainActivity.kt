package org.koin.sample.android.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.koin.benchmark.PerfLimit
import org.koin.benchmark.PerfRunner.runAll
import org.koin.sample.android.R
import org.koin.sample.android.main.MainApplication.Companion.limits
import org.koin.sample.android.main.MainApplication.Companion.results
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.Default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        title = "Android First Perfs"

    }

    override fun onStart() {
        super.onStart()
        extractResults()
    }

    private fun extractResults() {
        println("Perf Tolerance: $limits")
        val (startTime, execTime) = results!!
        val ok = results!!.isOk

        val textWidget = findViewById<TextView>(R.id.text)

        var textReport = """
                    Start time: $startTime - max ${results?.worstMaxStartTime} ms
                    Exec time: $execTime - max ${results?.worstExecTime} ms
                """.trimIndent()

        if (!ok) textReport += "\nTest Failed!"

        textWidget.text = textReport

        val color = if (!ok) {
            android.R.color.holo_red_dark
        } else android.R.color.holo_green_dark

        textWidget.setTextColor(resources.getColor(color))
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}