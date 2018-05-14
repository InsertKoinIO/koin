package org.koin.core


/**
 * Module callback
 */
interface ModuleCallback {

    /**
     * Notify on module release
     * @param path - module path
     */
    fun onRelease(path: String)
}
