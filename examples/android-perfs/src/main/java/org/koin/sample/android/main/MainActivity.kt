package org.koin.sample.android.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.koin.dsl.koinApplication
import org.koin.perfs.Perfs
import org.koin.perfs.perfModule400
//import org.koin.perfs.perfModule400
//import org.koin.perfs.perfModule400Ext
import org.koin.sample.android.R
import kotlin.time.DurationUnit
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runBlocking(Dispatchers.Default) {

            val launchs = (1..10).map { i ->
                GlobalScope.async {
                    runPerf(i)
                }.await()
            }
            val avgStart = launchs.map { it.first }.sum() / launchs.size
            val avgExec = launchs.map { it.second }.sum() / launchs.size
            println("Avg start time: $avgStart")
            println("Avg execution time: $avgExec")
        }

        setContentView(R.layout.main_activity)
        title = "Android First Perfs"
    }

    fun runPerf(count: Int): Pair<Double, Double> {
        val (app, duration) = measureTimedValue {
            koinApplication {
                modules(perfModule400())
            }
        }

        val durationInMillis = duration.toDouble(DurationUnit.MILLISECONDS)
        println("[$count] started in $durationInMillis ms")

        val koin = app.koin

        val executionDuration = measureTime {
            koin.get<Perfs.A27>()
            koin.get<Perfs.A31>()
            koin.get<Perfs.A12>()
            koin.get<Perfs.A42>()
        }
        val executionDurationInMillis = executionDuration.toDouble(DurationUnit.MILLISECONDS)

        println("[$count] measured executed in $executionDurationInMillis ms")
        app.close()
        return Pair(durationInMillis, executionDurationInMillis)
    }
}