package org.koin.sample.android.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.koin.core.time.measureDurationForResult
import org.koin.dsl.koinApplication
import org.koin.perfs.Perfs
import org.koin.perfs.perfModule400
import org.koin.sample.android.R

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
        val (app, duration) = measureDurationForResult {
            koinApplication {
                modules(perfModule400())
            }
        }
        println("[$count] started in $duration ms")

        val koin = app.koin

        val (_, executionDuration) = measureDurationForResult {
            koin.get<Perfs.A27>()
            koin.get<Perfs.A31>()
            koin.get<Perfs.A12>()
            koin.get<Perfs.A42>()
        }
        println("[$count] measured executed in $executionDuration ms")
        app.close()
        return Pair(duration, executionDuration)
    }
}