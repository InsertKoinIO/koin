package org.koin.sample.androidx.utils

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.io.Serializable

inline fun <reified T : AppCompatActivity> Context.navigateTo(isRoot: Boolean = false, extras: Map<String, Any> = emptyMap()) {
    val intent = intentFor<T>()
    intent.apply {
        applyExtras(extras)
    }
    val callWithFlag = if (isRoot) intent.clearTop().clearTask().newTask() else intent
    startActivity(callWithFlag)
}

inline fun <reified T : AppCompatActivity> AppCompatActivity.navigateTo(isRoot: Boolean = false, extras: Map<String, Any> = emptyMap()) {
    val intent = intentFor<T>()
    intent.apply {
        applyExtras(extras)
    }
    val callWithFlag = if (isRoot) intent.clearTop().clearTask().newTask() else intent
    startActivity(callWithFlag)
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
