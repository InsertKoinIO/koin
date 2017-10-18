package org.koin.android

import android.support.v4.app.Fragment
import org.koin.standalone.releaseContext

abstract class ContextAwareFragment() : Fragment() {

    abstract val contextName: String

    override fun onPause() {
        releaseContext(contextName)
        super.onPause()
    }
}