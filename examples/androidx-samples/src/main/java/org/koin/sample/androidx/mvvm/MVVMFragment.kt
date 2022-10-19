package org.koin.sample.androidx.mvvm

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.ScopeFragment
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.scope.requireScopeActivity
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.scope.emptyState
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.sample.android.R
import org.koin.sample.androidx.components.ID
import org.koin.sample.androidx.components.mvvm.ExtSimpleViewModel
import org.koin.sample.androidx.components.mvvm.SavedStateViewModel
import org.koin.sample.androidx.components.mvvm.SimpleViewModel
import org.koin.sample.androidx.components.scope.Session

class MVVMFragment(private val session: Session) : Fragment(R.layout.mvvm_fragment), AndroidScopeComponent {

    override val scope: Scope by fragmentScope()

    val simpleViewModel: SimpleViewModel by viewModel { parametersOf(ID) }
    val scopeVm: ExtSimpleViewModel by viewModel()
    val extScopeVm: ExtSimpleViewModel by viewModel(named("ext"))

    val shared: SimpleViewModel by sharedViewModel()// sharedViewModel { parametersOf(ID) }

    val sharedSaved: SavedStateViewModel by sharedViewModel { parametersOf(ID) }
    val saved by stateViewModel<SavedStateViewModel>(state = emptyState()) { parametersOf(ID) }
    val saved2 by stateViewModel<SavedStateViewModel>(state = emptyState()) { parametersOf(ID) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shared2 = getSharedViewModel<SimpleViewModel> { parametersOf(ID) }

        checkNotNull(session)

        assert(shared != simpleViewModel)
        assert((requireActivity() as MVVMActivity).simpleViewModel == shared)

        assert((requireActivity() as MVVMActivity).savedVm != saved)
        assert((requireActivity() as MVVMActivity).savedVm != saved2)
        assert(scopeVm.session.id == extScopeVm.session.id)
        assert((requireActivity() as MVVMActivity).savedVm == sharedSaved)

        assert(shared == shared2)

        assert(saved == saved2)

        assert(requireScopeActivity<MVVMActivity>().get<Session>().id == getKoin().getProperty("session_id"))
        assert(scope.get<Session>().id == requireScopeActivity<MVVMActivity>().get<Session>().id)
    }
}
