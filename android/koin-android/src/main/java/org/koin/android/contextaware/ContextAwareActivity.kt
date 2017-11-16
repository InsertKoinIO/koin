package org.koin.android.contextaware

import android.support.v7.app.AppCompatActivity
import org.koin.Koin
import org.koin.android.ext.android.releaseContext

/**
 * ContextAwareActivity
 * Help to Drop context with name contextName on onPause()
 *
 * @author Arnaud Giuliani
 */
abstract class ContextAwareActivity(override val contextName: String, override val contextDrop: ContextDropMethod = ContextAwareConfig.defaultContextDrop) : ContextAwareComponent, AppCompatActivity() {

    override fun onPause() {
        if (contextDrop == ContextDropMethod.OnPause) {
            Koin.logger.log("Activity ContextAware - drop on onPause")
            releaseContext(contextName)
        }
        super.onPause()
    }

    override fun onDestroy() {
        if (contextDrop == ContextDropMethod.OnDestroy) {
            Koin.logger.log("Activity ContextAware - drop on onDestroy")
            releaseContext(contextName)
        }
        super.onDestroy()
    }
}