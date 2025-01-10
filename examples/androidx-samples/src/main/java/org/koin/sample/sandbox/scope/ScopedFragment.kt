package org.koin.sample.sandbox.scope

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.fragmentScope
import org.koin.core.scope.Scope
import org.koin.sample.sandbox.R
import org.koin.sample.sandbox.components.scope.Session

class ScopedFragment : Fragment(R.layout.mvvm_fragment), AndroidScopeComponent {

    override val scope: Scope by fragmentScope()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checks()
    }

    private fun checks() {
        assert(
            scope.get<Session>() == (requireActivity() as AndroidScopeComponent).scope.get<Session>()
        )
    }
}