package org.koin.androidx.scope

import androidx.lifecycle.ViewModel
import org.koin.core.scope.Scope

class ScopeHandlerViewModel : ViewModel() {

    var scope: Scope? = null

    override fun onCleared() {
        super.onCleared()
        scope?.apply {
            logger.debug("Closing scope $scope")
            close()
        }
        scope = null
    }
}