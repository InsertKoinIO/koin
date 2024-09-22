package org.koin.android.test.verify

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkerParameters
import org.koin.android.test.verify.AndroidVerify.androidTypes
import org.koin.core.module.Module
import org.koin.test.verify.MissingKoinDefinitionException
import org.koin.test.verify.ParameterTypeInjection
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
fun Module.verify(extraTypes: List<KClass<*>> = emptyList(), injections: List<ParameterTypeInjection>? = null) {
    org.koin.test.verify.Verify.verify(this, extraTypes + androidTypes, injections)
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
fun Module.androidVerify(extraTypes: List<KClass<*>> = emptyList(), injections: List<ParameterTypeInjection>? = null) {
    org.koin.test.verify.Verify.verify(this, extraTypes + androidTypes, injections)
}

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
