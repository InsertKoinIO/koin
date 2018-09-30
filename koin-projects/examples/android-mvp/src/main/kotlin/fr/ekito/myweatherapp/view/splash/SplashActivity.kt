package fr.ekito.myweatherapp.view.splash

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AnimationUtils
import fr.ekito.myweatherapp.R
import fr.ekito.myweatherapp.view.weather.WeatherActivity
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.koin.android.ext.android.inject

/**
 * Search Weather View
 */
class SplashActivity : AppCompatActivity(), SplashContract.View {

    // Presenter
    override val presenter: SplashContract.Presenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onStart() {
        super.onStart()
        // Bind View
        presenter.subscribe(this)
        presenter.getLastWeather()
    }

    override fun onStop() {
        presenter.unSubscribe()
        super.onStop()
    }

    override fun showIsLoading() {
        val animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.infinite_blinking_animation)
        splashIcon.startAnimation(animation)
    }

    override fun showIsLoaded() {
        startActivity(intentFor<WeatherActivity>().clearTop().clearTask().newTask())
    }

    override fun showError(error: Throwable) {
        splashIcon.visibility = View.GONE
        splashIconFail.visibility = View.VISIBLE
        Snackbar.make(splash, "SplashActivity got error : $error", Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) {
                presenter.getLastWeather()
            }
            .show()
    }
}
