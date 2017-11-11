package org.koin.android.contextaware

import android.support.v7.app.AppCompatActivity
import org.koin.android.ext.android.releaseContext

/**
 * ContextAwareActivity
 * Help to Drop context with name contextName on onPause()
 *
 * @author Arnaud Giuliani
 */
abstract class ContextAwareActivity : AppCompatActivity() {

    /**
     * Koin sub context name
     */
    abstract val contextName: String

    override fun onPause() {
        releaseContext(contextName)
        super.onPause()
    }
}