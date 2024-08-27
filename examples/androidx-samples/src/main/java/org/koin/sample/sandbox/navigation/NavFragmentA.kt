package org.koin.sample.sandbox.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import org.koin.androidx.navigation.koinNavGraphViewModel
import org.koin.sample.sandbox.R
import org.koin.sample.sandbox.navigation.NavFragmentB.Companion

class NavFragmentA : Fragment() {

    val mainViewModel: NavViewModel by koinNavGraphViewModel(R.id.main)

    lateinit var button: Button
    var int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.nav_fragment_a, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button = requireView().findViewById(R.id.a_button)

        button.setOnClickListener {
            int++
            println("A -> $int")
            findNavController().navigate(R.id.action_fragmentA_to_fragmentB,
                Bundle().apply { putString("argKey", "value+$int") })
        }

        ID = mainViewModel.id
        println("A vm id: ${NavFragmentB.ID}")
    }

    companion object {
        var ID = ""
    }
}