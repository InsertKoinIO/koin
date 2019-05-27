package org.koin.sample.android.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.koin.core.time.measureDuration
import org.koin.dsl.koinApplication
import org.koin.perfs.Perfs
import org.koin.perfs.perfModule400
import org.koin.perfs.perfModule400Ext
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
            val avg = launchs.map { it.second }.sum() / launchs.size
            println("Avg execution time: $avg")
        }

        setContentView(R.layout.main_activity)
        title = "Android First Perfs"
    }

    fun runPerf(count: Int): Pair<Double, Double> {
        val (app, duration) = measureDuration {
            koinApplication {
                modules(perfModule400())
            }
        }
        println("[$count] started in $duration ms")

        val koin = app.koin

        val (_, executionDuration) = measureDuration {
            koin.get<Perfs.A27>()
            koin.get<Perfs.B31>()
            koin.get<Perfs.C12>()
            koin.get<Perfs.D42>()
        }
        println("[$count] measured executed in $executionDuration ms")
        app.close()
        return Pair(duration, executionDuration)
    }
}