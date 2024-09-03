package org.koin.test.verify

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.Module
import kotlin.reflect.KClass
import kotlin.time.measureTime

/**
 * Make a static verification about all declared classes constructors, to ensure they are all bound to an existing definition
 * Throws MissingDefinitionException if any definition is missing
 *
 * @param extraTypes - allow to declare extra type, to be bound above the existing definitions
 * @throws MissingKoinDefinitionException
 */
@KoinExperimentalAPI
fun Module.verify(extraTypes: List<KClass<*>> = listOf()) = Verify.verify(this, extraTypes)

/**
 * Verify a list of Modules
 *
 * @see Module.verify
 */
@KoinExperimentalAPI
fun List<Module>.verifyAll(extraTypes: List<KClass<*>> = listOf()) {
    forEach { module -> module.verify(extraTypes) }
}

/**
 * Verify API
 *
 * Help to check current factory of a Module
 */
@OptIn(KoinInternalApi::class)
@KoinExperimentalAPI
object Verify {

    internal val primitiveTypes = listOf(
        String::class,
        Int::class,
        Long::class,
        Double::class,
    )

    internal val whiteList = arrayListOf<KClass<*>>().apply {
        addAll(primitiveTypes)
    }

    fun addExtraTypes(vararg kClass: KClass<*>) {
        whiteList.addAll(kClass)
    }

    fun addExtraTypes(kClassList: List<KClass<*>>) {
        whiteList.addAll(kClassList)
    }

    /**
     * Make a static verification about all declared classes constructors, to ensure they are all bound to an existing definition
     * Throws MissingDefinitionException if any definition is missing
     *
     * @param module the moduel to verify
     * @param extraTypes allow to declare extra type, to be bound above the existing definitions
     * @throws MissingKoinDefinitionException
     */
    fun verify(module: Module, extraTypes: List<KClass<*>> = listOf()) {
        val duration = measureTime {
            val verification = Verification(module, extraTypes)
            println("Verifying module '$module' ...")
//            println("- index: ${verification.definitionIndex.size}")
            verification.verify()
        }

        println("\n[SUCCESS] module '$this' has been verified in $duration.")
    }
}
