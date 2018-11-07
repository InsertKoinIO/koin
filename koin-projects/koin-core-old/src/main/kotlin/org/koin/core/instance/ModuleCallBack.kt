package org.koin.core.instance

/**
 * Handle Path release
 * Deprecated - Use Scope APi
 */
@Deprecated("Please use Scope API instead.")
interface ModuleCallBack {

    fun onRelease(path: String)
}