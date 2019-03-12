package org.koin.sample.navigation


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.view_five.*
import org.koin.android.viewmodel.ext.android.sharedViewModel


/**
 * A simple [Fragment] subclass.
 *
 */
class FiveView : Fragment() {

    private val vm: FourFiveSharedViewModel by sharedViewModel(from = { parentFragment!! })

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.view_five, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text5.text = "Five View with shared value: (${vm.sharedValue})"
    }
}

