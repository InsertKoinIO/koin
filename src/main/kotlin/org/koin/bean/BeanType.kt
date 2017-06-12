package org.koin.bean

/**
 * Bean Types
 *
 * @author - Arnaud GIULIANI
 *
 * SINGLETON: one instance only in the context
 * FACTORY : one instance per call (old isntance is removed)
 * STACK : one instance, then removed from context
 */
enum class BeanType {
    SINGLETON, FACTORY, STACK
}