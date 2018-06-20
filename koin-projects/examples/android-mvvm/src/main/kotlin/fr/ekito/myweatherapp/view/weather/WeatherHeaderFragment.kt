package fr.ekito.myweatherapp.view.weather

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import fr.ekito.myweatherapp.R
import fr.ekito.myweatherapp.domain.DailyForecastModel
import fr.ekito.myweatherapp.domain.getColorFromCode
import fr.ekito.myweatherapp.view.IntentArguments
import fr.ekito.myweatherapp.view.detail.DetailActivity
import kotlinx.android.synthetic.main.fragment_result_header.*
import org.jetbrains.anko.startActivity
import org.koin.android.viewmodel.ext.android.sharedViewModel

class WeatherHeaderFragment : Fragment() {

    /*
     * Declare shared WeatherViewModel with WeatherActivity
     */
    private val viewModel by sharedViewModel<WeatherViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_result_header, container, false) as ViewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Observe WeatherListState
        viewModel.states.observe(this, Observer { state ->
            state?.let {
                when (state) {
                    is WeatherViewModel.WeatherListState -> showWeather(state.location, state.first)
                }
            }
        })
        // Observe LoadingLocationEvent & LoadLocationFailedEvent
        viewModel.events.observe(this, Observer { event ->
            event?.let {
                when (event) {
                    is WeatherViewModel.LoadingLocationEvent -> showLoadingLocation(event.location)
                    is WeatherViewModel.LoadLocationFailedEvent -> showLocationSearchFailed(
                        event.location,
                        event.error
                    )
                }
            }
        })
    }

    private fun showWeather(location: String, weather: DailyForecastModel) {
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
                IntentArguments.ARG_WEATHER_ITEM_ID to weather.id
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
            viewModel.loadNewLocation(newLocation)
        }
        dialog.setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        dialog.show()
    }

    private fun showLoadingLocation(location: String) {
        Snackbar.make(
            weatherHeader,
            getString(R.string.loading_location) + " $location ...",
            Snackbar.LENGTH_LONG
        )
            .show()
    }

    private fun showLocationSearchFailed(location: String, error: Throwable) {
        Snackbar.make(
            weatherHeader,
            getString(R.string.loading_error) + " $error",
            Snackbar.LENGTH_LONG
        )
            .setAction(R.string.retry, {
                viewModel.loadNewLocation(location)
            })
            .show()
    }
}