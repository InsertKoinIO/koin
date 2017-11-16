package org.koin.android.contextaware

import android.support.v4.app.Fragment
import org.koin.Koin
import org.koin.android.ext.android.releaseContext

/**
 * ContextAwareFragment
 *
 * Drop context with name contextName on onPause()
 *
 * @author Arnaud Giuliani
 */
abstract class ContextAwareFragment(override val contextName: String, override val contextDrop: ContextDropMethod = ContextAwareConfig.defaultContextDrop) : ContextAwareComponent, Fragment() {

    override fun onPause() {
        if (contextDrop == ContextDropMethod.OnPause) {
            Koin.logger.log("Fragment ContextAware - drop on onPause")
            releaseContext(contextName)
        }
        super.onPause()
    }

    override fun onDestroy() {
        if (contextDrop == ContextDropMethod.OnDestroy) {
            Koin.logger.log("Fragment ContextAware - drop on onDestroy")
            releaseContext(contextName)
        }
        super.onDestroy()
    }
}