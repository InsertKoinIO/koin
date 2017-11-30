package org.koin.test

import org.junit.After
import org.koin.Koin
import org.koin.KoinContext
import org.koin.error.BeanInstanceCreationException
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.closeKoin

/**
 * Koin Test Component
 */
interface KoinTest : KoinComponent

/**
 * Koin Test - embed autoclose @after method to close Koin after every test
 */
abstract class AbstractKoinTest(val autoCloseKoin: Boolean = true) : KoinTest {
    @After
    fun autoClose() {
        if (autoCloseKoin) {
            Koin.logger.log("AutoClose Koin")
            closeKoin()
        }
    }
}

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
 * Make a Dry Run - Test if each definition is injectable
 */
fun KoinTest.dryRun() {
    (StandAloneContext.koinContext as KoinContext).dryRun()
}