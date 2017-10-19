package org.koin.android.contextaware

import android.support.v4.app.Fragment
import org.koin.standalone.releaseContext

/**
 * Fragment - Drop context with name contextName on onPause()
 */
abstract class ContextAwareFragment : Fragment() {

    abstract val contextName: String

    override fun onPause() {
        releaseContext(contextName)
        super.onPause()
    }
}