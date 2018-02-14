package org.koin.android.ext.android

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import java.io.Serializable

/**
 * Start an Activity for given class T and allow to work on intent with "run" lambda function
 */
inline fun <reified T : FragmentActivity> FragmentActivity.startActivity(run: Intent.() -> Unit) {
    val i = Intent(this, T::class.java)
    i.run()
    startActivity(i)
}

/**
 * Define withArguments inside intent
 */
fun Intent.withArguments(vararg arguments: Pair<String, Serializable>): Intent {
    arguments.forEach {
        putExtra(it.first, it.second)
    }
    return this
}

/**
 * Start an Activity for given class T and allow to work on intent with "run" lambda function
 */
fun Fragment.withArguments(vararg arguments: Pair<String, Serializable>): Fragment {
    val bundle = Bundle()
    arguments.forEach { bundle.putSerializable(it.first, it.second) }
    this.arguments = bundle
    return this
}

/**
 * Retrieve property from intent
 */
fun <T : Serializable> FragmentActivity.argument(key: String) = lazy { intent.extras[key] as T }

/**
 * Retrieve property with default value from intent
 */
fun <T : Serializable> FragmentActivity.argument(key: String, defaultValue: T? = null) = lazy {
    intent.extras[key] as? T ?: defaultValue
}

/**
 * Retrieve property from intent
 */
fun <T : Serializable> Fragment.argument(key: String) = lazy { arguments[key] as T }

/**
 * Retrieve property with default value from intent
 */
fun <T : Serializable> Fragment.argument(key: String, defaultValue: T? = null) = lazy {
    arguments[key]  as? T ?: defaultValue
}
