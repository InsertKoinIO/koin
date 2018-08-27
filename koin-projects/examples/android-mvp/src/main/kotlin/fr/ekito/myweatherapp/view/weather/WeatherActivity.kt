package fr.ekito.myweatherapp.view.weather

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import fr.ekito.myweatherapp.R
import fr.ekito.myweatherapp.di.Memory
import kotlinx.android.synthetic.main.activity_result.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.koin.android.scope.ext.android.scopedWith
import org.koin.dsl.path.moduleName

/**
 * Weather Result View
 */
class WeatherActivity : AppCompatActivity() {

    val TAG = this::class.java.simpleName

    val shared = Memory.getShared()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        scopedWith(this::class.moduleName)

        println("GOT SHARED - $this got - $shared")
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                println("t$this DESTROY")
                Memory.release()
            }
        })

        val weatherTitleFragment = WeatherHeaderFragment()
        val resultListFragment = WeatherListFragment()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.weather_title, weatherTitleFragment)
            .commit()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.weather_list, resultListFragment)
            .commit()
    }

    fun showError(error: Throwable) {
        Log.e(TAG, "error $error while displaying weather")
        weather_views.visibility = View.GONE
        weather_error.visibility = View.VISIBLE
        Snackbar.make(
            weather_result,
            "WeatherActivity got error : $error",
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.retry) {
                startActivity(intentFor<WeatherActivity>().clearTop().clearTask().newTask())
            }
            .show()
    }
}
