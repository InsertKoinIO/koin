package org.koin.core


/**
 * Module callback
 * @author - Arnaud GIULIANI
 * @author - Laurent BARESSE
 */
interface ModuleCallback {

    /**
     * Notify on module release
     * @param path - module path
     */
    fun onRelease(path: String)
}
