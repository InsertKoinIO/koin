package org.koin.sample.navigation


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.view_third.*
import org.koin.android.scope.bindScope
import org.koin.android.scope.getFragmentScope


/**
 * A simple [Fragment] subclass.
 *
 */
class ThirdView : Fragment() {

    val vm: ThirdViewModel by getFragmentScope().inject()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.view_third, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindScope(getFragmentScope())

        activity?.title = "Third"

        text3.setOnClickListener {
            Navigation.findNavController(it)
                    .navigate(R.id.action_thirdView_to_fourFiveView)
        }

        vm.sayHello()
    }
}
