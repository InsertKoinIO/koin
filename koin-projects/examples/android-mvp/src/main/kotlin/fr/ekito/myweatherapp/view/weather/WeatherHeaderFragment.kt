package fr.ekito.myweatherapp.view.weather

import android.app.AlertDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import fr.ekito.myweatherapp.R
import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.domain.entity.getColorFromCode
import fr.ekito.myweatherapp.view.detail.DetailActivity
import fr.ekito.myweatherapp.view.detail.DetailActivity.Companion.INTENT_WEATHER_ID
import kotlinx.android.synthetic.main.fragment_result_header.*
import org.jetbrains.anko.*
import org.koin.android.ext.android.inject

class WeatherHeaderFragment : Fragment(), WeatherHeaderContract.View {

    override val presenter: WeatherHeaderContract.Presenter by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_result_header, container, false) as ViewGroup
    }

    override fun onResume() {
        presenter.subscribe(this)
        presenter.getWeatherOfTheDay()
        super.onResume()
    }

    override fun onPause() {
        presenter.unSubscribe()
        super.onPause()
    }

    override fun showWeather(location: String, weather: DailyForecast) {
        weatherCity.text = location
        weatherCityCard.setOnClickListener {
            promptLocationDialog()
        }

        weatherIcon.text = weather.icon
        weatherDay.text = weather.day
        weatherTempText.text = weather.temperature.toString()
        weatherText.text = weather.shortText

        val color = context!!.getColorFromCode(weather)
        weatherHeader.background.setTint(color)

        weatherHeader.setOnClickListener {
            activity?.startActivity<DetailActivity>(
                INTENT_WEATHER_ID to weather.id
            )
        }
    }

    private fun promptLocationDialog() {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(getString(R.string.enter_location))
        val editText = EditText(context)
        editText.hint = getString(R.string.location_hint)
        editText.maxLines = 1
        editText.inputType = InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
        dialog.setView(editText)
        dialog.setPositiveButton(getString(R.string.search)) { dialogInterface, _ ->
            dialogInterface.dismiss()
            val newLocation = editText.text.trim().toString()
            presenter.loadNewLocation(newLocation)
            Snackbar.make(
                weatherHeader,
                getString(R.string.loading_location) + " $newLocation ...",
                Snackbar.LENGTH_LONG
            )
                .show()
        }
        dialog.setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        dialog.show()
    }

    override fun showLocationSearchSucceed(location: String) {
        activity?.apply {
            startActivity(
                intentFor<WeatherActivity>().clearTop().clearTask().newTask()
            )
        }
    }

    override fun showLocationSearchFailed(location: String, error: Throwable) {
        Snackbar.make(weatherHeader, getString(R.string.loading_error), Snackbar.LENGTH_LONG)
            .setAction(R.string.retry) {
                presenter.loadNewLocation(location)
            }
            .show()
    }

    override fun showError(error: Throwable) {
        (activity as? WeatherActivity)?.showError(error)
    }
}