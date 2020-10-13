package org.koin.androidx.scope

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import org.koin.ext.getFullName

fun <T : Fragment> T.fragmentScope(): Scope {
    val scopeViewModel = viewModels<ScopeHandlerViewModel>().value
    if (scopeViewModel.scope == null) {
        scopeViewModel.scope = newScope(this)
    }
    return scopeViewModel.scope!!
}

fun <T : Fragment> T.getScopeId() = this::class.getFullName() + "@" + System.identityHashCode(this)
fun <T : Fragment> T.getScopeName() = TypeQualifier(this::class)
fun <T : Fragment> T.newScope(source: Any? = null): Scope {
    return getKoin().createScope(getScopeId(), getScopeName(), source)
}