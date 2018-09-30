package fr.ekito.myweatherapp.view.weather

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import fr.ekito.myweatherapp.R
import kotlinx.android.synthetic.main.activity_result.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

/**
 * Weather Result View
 */
class WeatherActivity : AppCompatActivity() {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

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
