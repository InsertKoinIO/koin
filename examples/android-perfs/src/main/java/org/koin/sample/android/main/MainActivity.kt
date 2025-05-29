package org.koin.sample.android.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
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

        val color = if (ok) {
            android.R.color.holo_green_dark
        } else {
            android.R.color.holo_red_dark
        }

        textWidget.setTextColor(ContextCompat.getColor(this, color))
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}
