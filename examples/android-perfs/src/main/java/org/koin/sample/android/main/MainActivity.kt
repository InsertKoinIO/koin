package org.koin.sample.android.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.koin.benchmark.PerfLimit
import org.koin.benchmark.PerfRunner.runAll
import org.koin.sample.android.R
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        title = "Android First Perfs"

        runBlocking {
            val limits = PerfLimit(10.8.milliseconds, 0.185.milliseconds)

            println("Perf Tolerance: $limits")

            val results = runAll(this)
            results.applyLimits(limits)
            val (startTime,execTime) = results

            val textWidget = findViewById<TextView>(R.id.text)
            var textReport = """
                Start time: $startTime - max ${results.worstMaxStartTime}
                Exec time: $execTime - max ${results.worstExecTime}
            """.trimIndent()

            if (!results.isOk) textReport += "\nTest Failed!"

            textWidget.text = textReport

            val color = if (!results.isOk) {
                android.R.color.holo_red_dark
            } else android.R.color.holo_green_dark

            textWidget.setTextColor(resources.getColor(color))
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}