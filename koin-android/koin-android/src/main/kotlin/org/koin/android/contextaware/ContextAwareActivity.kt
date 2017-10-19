package org.koin.android.contextaware

import android.support.v7.app.AppCompatActivity
import org.koin.standalone.releaseContext

/**
 * Activity - Drop context with name contextName on onPause()
 */
abstract class ContextAwareActivity : AppCompatActivity() {

    abstract val contextName: String

    override fun onPause() {
        releaseContext(contextName)
        super.onPause()
    }
}