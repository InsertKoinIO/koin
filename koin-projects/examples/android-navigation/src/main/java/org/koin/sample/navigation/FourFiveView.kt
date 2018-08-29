package org.koin.sample.navigation


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.view_four_five.*
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf


/**
 * A simple [Fragment] subclass.
 *
 */
class FourFiveView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getViewModel<FourFiveSharedViewModel> { parametersOf("DATA ONLY FOR WE FRAGMENTS") }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.view_four_five, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigateText.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_fourFiveView_to_firstView)
        }
    }
}

