package org.koin.error

/**
 * Bean instance resolution error
 *
 * @author - Arnaud GIULIANI
 * @author - Laurent BARESSE
 */
class DependencyResolutionException(msg : String) : Exception(msg)