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

class NavFragmentA : Fragment() {

    val mainViewModel: NavViewModel by koinNavGraphViewModel(R.id.main)

    lateinit var button: Button

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
            findNavController().navigate(R.id.action_fragmentA_to_fragmentB)
        }

        ID = mainViewModel.id
    }

    companion object {
        var ID = ""
    }
}