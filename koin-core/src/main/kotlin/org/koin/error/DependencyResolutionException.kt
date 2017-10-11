package org.koin.error

/**
 * Bean instance resolution error
 *
 * @author Arnaud GIULIANI
 */
class DependencyResolutionException(msg : String) : Exception(msg)