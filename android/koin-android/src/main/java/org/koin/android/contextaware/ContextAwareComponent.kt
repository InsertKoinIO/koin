package org.koin.android.contextaware

/**
 * Context Aware interface
 */
interface ContextAwareComponent {
    val contextName: String
    val contextDrop: ContextDropMethod
}

/**
 * Context Aware drop methods
 */
enum class ContextDropMethod {
    OnPause, OnDestroy
}

/**
 * Default configuration for ContextAwareComponent
 */
object ContextAwareConfig {
    var defaultContextDrop = ContextDropMethod.OnPause
}