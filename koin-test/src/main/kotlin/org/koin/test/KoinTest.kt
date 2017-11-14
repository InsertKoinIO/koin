package org.koin.test

import org.koin.Koin
import org.koin.KoinContext
import org.koin.dsl.module.Module
import org.koin.error.BeanInstanceCreationException
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext

interface KoinTest : KoinComponent

/**
 * Get a dependency
 * @throws NoBeanDefFoundException if definition can't be found
 * @throws BeanInstanceCreationException if cant create
 * @param name
 */
inline fun <reified T> KoinTest.get(name: String = "") = (StandAloneContext.koinContext as KoinContext).get<T>(name)

/**
 * Get a property
 * @throws MissingPropertyException if not found
 * @param name
 */
inline fun <reified T> KoinTest.getProperty(name: String = "") = (StandAloneContext.koinContext as KoinContext).getProperty<T>(name)

/**
 * Get a property - with default value if not found
 * @param name
 * @param defaultValue
 */
inline fun <reified T> KoinTest.getProperty(name: String = "", defaultValue: T) = (StandAloneContext.koinContext as KoinContext).getProperty(name, defaultValue)

/**
 * Dry Run Starter
 */
fun KoinTest.dryRun(modules: List<Module>) {
    StandAloneContext.koinContext = Koin().build(modules)
    (StandAloneContext.koinContext as KoinContext).dryRun()
}