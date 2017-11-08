package org.koin.android.contextaware

import android.support.v4.app.Fragment
import org.koin.standalone.releaseContext

/**
 * ContextAwareFragment
 *
 * Drop context with name contextName on onPause()
 *
 * @author Arnaud Giuliani
 */
abstract class ContextAwareFragment : Fragment() {

    /**
     * Koin sub context name
     */
    abstract val contextName: String

    override fun onPause() {
        releaseContext(contextName)
        super.onPause()
    }
}