package org.koin.androidx.scope

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import org.koin.ext.getFullName

fun <T : ComponentActivity> T.activityScope() : Scope {
    val scopeViewModel = viewModels<ScopeHandlerViewModel>().value
    if (scopeViewModel.scope == null){
        scopeViewModel.scope = newScope(this)
    }
    return scopeViewModel.scope!!
}

fun <T : ComponentActivity> T.getScopeId() = this::class.getFullName() + "@" + System.identityHashCode(this)
fun <T : ComponentActivity> T.getScopeName() = TypeQualifier(this::class)
fun <T : ComponentActivity> T.newScope(source: Any? = null): Scope {
    return getKoin().createScope(getScopeId(), getScopeName(), source)
}