package com.example.androidx_navigation.featurea

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androidx_navigation.R

class FeatureAFragment1 : Fragment(R.layout.fragment_feature_a_1) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.button) .setOnClickListener {
            findNavController().navigate(R.id.action_featureAFragment1_to_featureAFragment2)
        }
    }
}
