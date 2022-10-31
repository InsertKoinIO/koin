package org.koin.sample.android.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.koin.core.time.measureDurationForResult
import org.koin.dsl.koinApplication
import org.koin.sample.android.R
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runBlocking(Dispatchers.Default) {

            val launchs = (1..10).map { i -> async { runPerf(i) }.await() }
            val avgStart = launchs.map { it.first }.sum() / launchs.size
            val avgExec = launchs.map { it.second }.sum() / launchs.size
            println("Avg start time: $avgStart")
            println("Avg execution time: $avgExec")
        }

        setContentView(R.layout.main_activity)
        title = "Android First Perfs"
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    fun runPerf(count: Int): Pair<Double, Double> {
        val (app, duration) = measureDurationForResult {
            koinApplication {
                modules(perfModule400())
            }
        }
        println("Perf[$count] start in $duration ms")

        val koin = app.koin

        val (_, executionDuration) = measureDurationForResult {
            koin.get<A27>()
            koin.get<A31>()
            koin.get<A12>()
            koin.get<A42>()
        }
        println("Perf[$count] run in $executionDuration ms")
        app.close()
        return Pair(duration, executionDuration)
    }
}