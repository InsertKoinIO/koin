package org.koin.test.verify

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.Module
import org.koin.core.time.Timer
import kotlin.reflect.KClass

/**
 * Make a static verification about all declared classes constructors, to ensure they are all bound to an existing definition
 * Throws MissingDefinitionException if any definition is missing
 *
 * @param extraTypes - allow to declare extra type, to be bound above the existing definitions
 * @param manualVerification manual validation mode for additional types that cannot be passed through extraTypes for one reason or another. (For example, no access to type)
 * @throws MissingKoinDefinitionException
 */
@KoinExperimentalAPI
fun Module.verify(
    extraTypes: List<KClass<*>> = listOf(),
    manualVerification: ((String) -> Boolean)? = null,
) = Verify.verify(this, extraTypes, manualVerification)

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
     * @param manualVerification manual validation mode for additional types that cannot be passed through extraTypes for one reason or another. (For example, no access to type)
     * @throws MissingKoinDefinitionException
     */
    fun verify(
        module: Module,
        extraTypes: List<KClass<*>> = listOf(),
        manualVerification: ((String) -> Boolean)? = null,
    ) {
        val timer = Timer.start()

        val verification = Verification(module, extraTypes, manualVerification)
        println("Verifying module '$module' ...")
//        println("- index: ${verification.definitionIndex.size}")
        verification.verify()

        timer.stop()
        val time =
            if (timer.getTimeInMillis() < 1000) "${timer.getTimeInMillis()} ms" else "${timer.getTimeInSeconds()} sec"
        println("\n[SUCCESS] module '$this' has been verified in $time.")
    }
}
