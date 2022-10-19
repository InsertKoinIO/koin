package org.koin.androidx.viewmodel.factory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import org.koin.androidx.viewmodel.needSavedStateHandle
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

class KoinViewModelFactory(
    private val kClass: KClass<out ViewModel>,
    private val scope: Scope,
    private val qualifier: Qualifier? = null,
    private val params: ParametersDefinition? = null
) : ViewModelProvider.Factory {

    @OptIn(KoinInternalApi::class)
    private val needSSH: Boolean = kClass.java.needSavedStateHandle()

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val parameters: ParametersDefinition? = if (needSSH) {
            val ssh = extras.createSavedStateHandle()
            params?.addSSH(ssh) ?: { parametersOf(ssh) }
        } else params

        return scope.get(kClass, qualifier, parameters)
    }

    //TODO Avoid such insertion - need to see with internal core resolution
    private fun ParametersDefinition.addSSH(ssh: SavedStateHandle): ParametersDefinition {
        return { invoke().add(ssh) }
    }
}