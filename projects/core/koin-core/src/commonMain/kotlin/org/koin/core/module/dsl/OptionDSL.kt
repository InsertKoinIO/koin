@file:OptIn(KoinInternalApi::class)

package org.koin.core.module.dsl

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Callbacks
import org.koin.core.definition.KoinDefinition
import org.koin.core.definition.OnCloseCallback
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.module.OptionDslMarker
import org.koin.core.qualifier.StringQualifier
import org.koin.core.qualifier.TypeQualifier
import kotlin.reflect.KClass

typealias DefinitionOptions<T> = BeanDefinition<T>.() -> Unit

/**
 * Apply additional options to a definition using a DSL block.
 *
 * This allows you to configure qualifiers, bindings, lifecycle callbacks,
 * and other definition properties in a structured way.
 *
 * @param options Lambda with receiver on BeanDefinition to configure options
 * @return this definition for chaining
 *
 * @author Arnaud Giuliani
 *
 * Example:
 * ```kotlin
 * single { MyService() } withOptions {
 *     named("production")
 *     bind<ServiceInterface>()
 *     createdAtStart()
 *     override()
 * }
 * ```
 */
@OptionDslMarker
inline infix fun <T> KoinDefinition<T>.withOptions(
    options: DefinitionOptions<T>,
): KoinDefinition<T> {
    val def = factory.beanDefinition
    val primary = def.qualifier
    def.also(options)
    if (def.qualifier != primary) {
        module.indexPrimaryType(factory)
    }
    if (def.secondaryTypes.isNotEmpty()) {
        module.indexSecondaryTypes(factory)
    }
    if (def._createdAtStart && factory is SingleInstanceFactory<*>) {
        module.prepareForCreationAtStart(factory)
    }
    return this
}

/**
 * Conditionally apply options to a definition if the options parameter is not null.
 *
 * @param options Optional lambda with receiver to configure definition options
 * @return this definition for chaining
 */
fun <T> KoinDefinition<T>.onOptions(
    options: DefinitionOptions<T>? = null,
): KoinDefinition<T> {
    if (options != null) {
        withOptions(options)
    }
    return this
}

/**
 * Assign a string-based qualifier to this definition.
 * Used within `withOptions { }` block.
 *
 * @param name The string name to use as qualifier
 *
 * Example:
 * ```kotlin
 * single { MyService() } withOptions {
 *     named("production")
 * }
 * // Retrieve with: get(qualifier = named("production"))
 * ```
 */
fun BeanDefinition<*>.named(name: String) {
    qualifier = StringQualifier(name)
}

/**
 * Assign a type-based qualifier to this definition.
 * Used within `withOptions { }` block.
 *
 * The qualifier will be based on the reified type T.
 *
 * Example:
 * ```kotlin
 * single { MyService() } withOptions {
 *     named<ProductionQualifier>()
 * }
 * // Retrieve with: get(qualifier = named<ProductionQualifier>())
 * ```
 */
inline fun <reified T> BeanDefinition<*>.named() {
    qualifier = TypeQualifier(T::class)
}

/**
 * Mark this definition as allowing override, even when global allowOverride is false.
 * Used within `withOptions { }` block.
 *
 * This enables targeted overrides for specific definitions without opening up
 * all definitions to be overridden globally.
 *
 * Example:
 * ```kotlin
 * koinApplication {
 *     allowOverride(false)  // Strict mode - no overrides by default
 *     modules(
 *         module { single { ProductionService() } },
 *         module {
 *             single { TestService() } withOptions {
 *                 override()  // Only this definition can override
 *             }
 *         }
 *     )
 * }
 * ```
 */
inline fun <reified T> BeanDefinition<out T>.override() {
    allowOverride = true
}

/**
 * Add a secondary type binding to this definition.
 * Used within `withOptions { }` block.
 *
 * This allows the definition to be retrieved by an additional type,
 * typically an interface or superclass.
 *
 * Type-safety may be checked by "checkModules" from "koin-test" module.
 *
 * Example:
 * ```kotlin
 * single { MyServiceImpl() } withOptions {
 *     bind<ServiceInterface>()
 * }
 * // Can retrieve with: get<ServiceInterface>()
 * ```
 */
inline fun <reified T> BeanDefinition<out T>.bind() {
    secondaryTypes += T::class
}

/**
 * Add multiple secondary type bindings to this definition.
 * Used within `withOptions { }` block.
 *
 * @param classes List of classes to bind as additional types
 *
 * Example:
 * ```kotlin
 * single { MyServiceImpl() } withOptions {
 *     binds(listOf(ServiceInterface1::class, ServiceInterface2::class))
 * }
 * ```
 */
fun BeanDefinition<*>.binds(classes: List<KClass<*>>) {
    secondaryTypes += classes
}

/**
 * Mark this definition to be created eagerly at startup.
 * Used within `withOptions { }` block.
 *
 * Only applicable to singleton definitions. The instance will be created
 * immediately when the module is loaded, rather than lazily on first access.
 *
 * Example:
 * ```kotlin
 * single { DatabaseConnection() } withOptions {
 *     createdAtStart()
 * }
 * ```
 */
fun BeanDefinition<*>.createdAtStart() {
    _createdAtStart = true
}

/**
 * Register a callback to be invoked when this definition is closed/released.
 * Used within `withOptions { }` block.
 *
 * Useful for cleanup operations like closing connections, releasing resources, etc.
 *
 * @param onClose Callback function that receives the instance being closed
 *
 * Example:
 * ```kotlin
 * single { DatabaseConnection() } withOptions {
 *     onClose { connection ->
 *         connection.close()
 *     }
 * }
 * ```
 */
//TODO Look At Closeable
fun <T> BeanDefinition<T>.onClose(onClose: OnCloseCallback<T>) {
    callbacks = Callbacks(onClose)
}
