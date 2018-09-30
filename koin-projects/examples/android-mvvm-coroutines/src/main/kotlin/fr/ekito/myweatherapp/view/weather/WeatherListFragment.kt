package fr.ekito.myweatherapp.view.weather

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.ekito.myweatherapp.R
import fr.ekito.myweatherapp.view.detail.DetailActivity
import fr.ekito.myweatherapp.view.weather.list.WeatherItem
import fr.ekito.myweatherapp.view.weather.list.WeatherListAdapter
import kotlinx.android.synthetic.main.fragment_result_list.*
import org.jetbrains.anko.startActivity
import org.koin.android.viewmodel.ext.android.sharedViewModel

class WeatherListFragment : Fragment() {

    /*
     * Declare shared WeatherViewModel with WeatherActivity
     */
    private val viewModel by sharedViewModel<WeatherViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_result_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareListView()
        // Observe WeatherListState
        viewModel.states.observe(this, Observer { state ->
            state?.let {
                when (state) {
                    is WeatherViewModel.WeatherListState -> showWeatherItemList(state.lasts.map {
                        WeatherItem.from(
                            it
                        )
                    })
                }
            }
        })
    }

    private fun prepareListView() {
        weatherList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        weatherList.adapter = WeatherListAdapter(
            activity!!,
            emptyList(),
            ::onWeatherItemSelected
        )
    }

    private fun onWeatherItemSelected(resultItem: WeatherItem) {
        activity?.startActivity<DetailActivity>(
            DetailActivity.ARG_WEATHER_ITEM_ID to resultItem.id
        )
    }

    private fun showWeatherItemList(newList: List<WeatherItem>) {
        val adapter: WeatherListAdapter = weatherList.adapter as WeatherListAdapter
        adapter.list = newList
        adapter.notifyDataSetChanged()
    }
}