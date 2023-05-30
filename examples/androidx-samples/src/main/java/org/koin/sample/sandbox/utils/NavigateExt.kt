package org.koin.sample.sandbox.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import java.io.Serializable

inline fun <reified T : Activity> Context.navigateTo(isRoot: Boolean = false, extras: Map<String, Any> = emptyMap()) {
    val intent = Intent(this,T::class.java)
    intent.apply {
        applyExtras(extras)
    }
    startActivity(intent)
}

inline fun <reified T : Activity> AppCompatActivity.navigateTo(isRoot: Boolean = false, extras: Map<String, Any> = emptyMap()) {
    val intent = Intent(this,T::class.java)
    intent.apply {
        applyExtras(extras)
    }
    startActivity(intent)
}

fun Intent.applyExtras(extras: Map<String, Any>) {
    extras.keys.forEach { key ->
        val value: Any? = extras[key]
        when (value) {
            is Int -> putExtra(key, value)
            is Long -> putExtra(key, value)
            is String -> putExtra(key, value)
            is Parcelable -> putExtra(key, value)
            is Serializable -> putExtra(key, value)
            else -> error("can't apply extra $key - unknown type")
        }
    }
}

inline fun <reified T : AppCompatActivity> Fragment.navigateTo(isRoot: Boolean = false, extras: Map<String, Any> = emptyMap()) {
    activity?.navigateTo<T>(isRoot, extras) ?: error("parent activity is null")
}
