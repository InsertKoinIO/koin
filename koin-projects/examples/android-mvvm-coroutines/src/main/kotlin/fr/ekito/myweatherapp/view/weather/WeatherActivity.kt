package fr.ekito.myweatherapp.view.weather

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import fr.ekito.myweatherapp.R
import fr.ekito.myweatherapp.view.ErrorState
import kotlinx.android.synthetic.main.activity_result.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Weather Result View
 */
class WeatherActivity : AppCompatActivity() {

    /*
     * Declare WeatherViewModel with Koin and allow constructor dependency injection
     */
    private val viewModel by viewModel<WeatherViewModel>()

    val TAG = this::class.java.simpleName

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

        // Observe ErrorState
        viewModel.states.observe(this, Observer { state ->
            state?.let {
                when (state) {
                    is ErrorState -> showError(state.error)
                }
            }
        })
        // Launch load of weather data
        viewModel.getWeather()
    }

    private fun showError(error: Throwable) {
        Log.e(TAG, "error $error while displaying weather")
        weather_views.visibility = View.GONE
        weather_error.visibility = View.VISIBLE
        Snackbar.make(
            weather_result,
            getString(R.string.loading_error) + "  $error",
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.retry) {
                startActivity(intentFor<WeatherActivity>().clearTop().clearTask().newTask())
            }
            .show()
    }
}
