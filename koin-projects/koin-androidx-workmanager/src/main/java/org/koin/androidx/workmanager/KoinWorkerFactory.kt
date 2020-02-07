package org.koin.androidx.workmanager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import org.jetbrains.annotations.TestOnly
import org.koin.core.KoinComponent
import org.koin.core.definition.Kind
import org.koin.core.error.NoScopeDefFoundException
import org.koin.core.parameter.DefinitionParameters
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.registry.ScopeRegistry
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeDefinition
import org.koin.ext.scope
import java.util.concurrent.atomic.AtomicLong

/**
 * Provides an implementation of [WorkerFactory] that ties into Koin DI.
 *
 * It has access to an [WorkerParameters] object, and  creates a local scope with access to it.
 * Then such scope is used to instantiate an object of [ListenableWorker] through koin.
 *
 * @author : Fabio de Matos
 * @since : 16/02/2020
 **/
class KoinWorkerFactory : WorkerFactory(), KoinComponent {

    companion object {

        /**
         * Android framework uses [Class::canonicalName] to identify the class names it wants created.
         */
        inline fun <reified T : ListenableWorker> getQualifier(): Qualifier = //named("yes")
            named(T::class.qualifiedName ?: "ANONYMOUS_CLASS")

        fun getQualifier(workerClassName: String): Qualifier =  //named("yes")
            named(workerClassName)
    }

    private val workManagerKoinCounter = AtomicLong(0)


    override fun createWorker(
        applicationContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        val newScope = createWorkParameterScope(applicationContext, workerParameters)

        return newScope
            .get<ListenableWorker>(
                qualifier = getQualifier(workerClassName)
            )
            .also {
                newScope.close()
            }
    }


    @TestOnly
    fun createWorkParameterScope(
        applicationContext: Context,
        workerParameters: WorkerParameters
    ): Scope {

        val scopeId = "work_manager_" + workManagerKoinCounter.incrementAndGet()

        try {
            val newScope = getKoin()
                .createScope<KoinWorkerFactory>(scopeId)

            newScope
                .also { newScope ->

                    getKoin()._scopeRegistry.rootScope._scopeDefinition
//                        ?.also {
//                            newScope.loadDefinitions(it)
//                        }
                        .let { it.definitions }
//                        .filter { it.kind == Kind.Factory }
                        .filter { it.hasType(ListenableWorker::class) }
                        .forEach {
                            newScope._instanceRegistry.saveDefinition(it, false)
                        }

                }
                .also {

                    if (!isContextDefined(it)) {
                        it.declare(applicationContext, override = false)
                    }
                    it.declare(workerParameters, override = true)

                }

            return newScope
        } catch (e: NoScopeDefFoundException) {
            print("Missing initialization")
            throw e
        }
    }

    private fun isContextDefined(scope: Scope): Boolean {
        return scope.getOrNull<Context>() != null
    }
}

