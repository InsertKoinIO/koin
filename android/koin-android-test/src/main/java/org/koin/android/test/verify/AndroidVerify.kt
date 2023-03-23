package org.koin.android.test.verify

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkerParameters
import org.koin.android.test.verify.AndroidVerify.androidTypes
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import kotlin.reflect.KClass

/**
 * Make a static verification about all declared classes constructors, to ensure they are all bound to an existing definition
 * Throws MissingDefinitionException if any definition is missing
 *
 * include Android default types
 *
 * @param extraTypes - allow to declare extra type, to be bound above the existing definitions
 * @throws MissingKoinDefinitionException
 */
@KoinExperimentalAPI
fun Module.verify(extraTypes: List<KClass<*>> = listOf()) {
    org.koin.test.verify.Verify.verify(this,extraTypes + androidTypes)
}

/**
 * Make a static verification about all declared classes constructors, to ensure they are all bound to an existing definition
 * Throws MissingDefinitionException if any definition is missing
 *
 * include Android default types
 *
 * equivalent of Module.verify
 *
 * @param extraTypes - allow to declare extra type, to be bound above the existing definitions
 * @throws MissingKoinDefinitionException
 */
@KoinExperimentalAPI
fun Module.androidVerify(extraTypes: List<KClass<*>> = listOf()) {
    org.koin.test.verify.Verify.verify(this,extraTypes + androidTypes)
}

@KoinExperimentalAPI
object AndroidVerify {
    val androidTypes = listOf(
        Context::class,
        Activity::class,
        Fragment::class,
        Application::class,
        SavedStateHandle::class,
        WorkerParameters::class
    )
}