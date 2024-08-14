package org.koin.sample.sandbox.mvvm

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.scope.requireScopeActivity
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.getActivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.sample.sandbox.R
import org.koin.sample.sandbox.components.ID
import org.koin.sample.sandbox.components.mvvm.SavedStateViewModel
import org.koin.sample.sandbox.components.mvvm.SharedVM
import org.koin.sample.sandbox.components.mvvm.SimpleViewModel
import org.koin.sample.sandbox.components.scope.Session

class MVVMFragment(private val session: Session) : Fragment(R.layout.mvvm_fragment), AndroidScopeComponent {

    override val scope: Scope by fragmentScope()

    val simpleViewModel: SimpleViewModel by viewModel { parametersOf(ID) }
    val shared: SimpleViewModel by activityViewModel { parametersOf(ID) }

    val saved by viewModel<SavedStateViewModel> { parametersOf(ID) }
    val saved2 by viewModel<SavedStateViewModel> { parametersOf(ID) }

    val sharedVM : SharedVM by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checks()
    }

    private fun checks() {
        checkNotNull(session)
        assert(shared != simpleViewModel)

        assert((requireActivity() as MVVMActivity).savedVm != saved)
        assert((requireActivity() as MVVMActivity).savedVm != saved2)


        val shared2 = getActivityViewModel<SimpleViewModel> { parametersOf(ID) }
        assert(shared == shared2)

        assert(saved == saved2)

        assert(requireScopeActivity<MVVMActivity>().get<Session>().id == getKoin().getProperty("session_id"))
        assert(scope.get<Session>().id == requireScopeActivity<MVVMActivity>().get<Session>().id)

        println("fragment sharedVM.session:${sharedVM.session}")
    }
}
