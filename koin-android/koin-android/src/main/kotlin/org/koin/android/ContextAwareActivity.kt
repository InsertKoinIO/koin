package org.koin.android

import android.support.v7.app.AppCompatActivity
import org.koin.standalone.releaseContext


abstract class ContextAwareActivity : AppCompatActivity() {

    abstract val contextName: String

    override fun onPause() {
        releaseContext(contextName)
        super.onPause()
    }
}