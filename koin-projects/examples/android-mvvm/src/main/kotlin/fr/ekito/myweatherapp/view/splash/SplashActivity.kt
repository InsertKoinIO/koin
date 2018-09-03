package fr.ekito.myweatherapp.view.splash

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AnimationUtils
import fr.ekito.myweatherapp.R
import fr.ekito.myweatherapp.view.FailedEvent
import fr.ekito.myweatherapp.view.LoadingEvent
import fr.ekito.myweatherapp.view.SuccessEvent
import fr.ekito.myweatherapp.view.weather.WeatherActivity
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Search Weather View
 */
class SplashActivity : AppCompatActivity() {

    // Declare SplashViewModel with Koin and allow constructor dependency injection
    private val viewModel by viewModel<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewModel.events.observe(this, Observer { event ->
            event?.let {
                when (event) {
                    LoadingEvent -> showIsLoading()
                    SuccessEvent -> showIsLoaded()
                    is FailedEvent -> showError(event.error)
                }
            }
        })
        viewModel.getLastWeather()
    }

    private fun showIsLoading() {
        val animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.infinite_blinking_animation)
        splashIcon.startAnimation(animation)
    }

    private fun showIsLoaded() {
        startActivity(intentFor<WeatherActivity>().clearTop().clearTask().newTask())
    }

    private fun showError(error: Throwable) {
        splashIcon.visibility = View.GONE
        splashIconFail.visibility = View.VISIBLE
        Snackbar.make(
            splash,
            getString(R.string.loading_error) + " $error",
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.retry, {
                viewModel.getLastWeather()
            })
            .show()
    }
}
