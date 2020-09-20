//package org.koin.androidx.workmanager
//
//import android.content.Context
//import androidx.work.WorkerParameters
//import org.junit.Assert.*
//import org.junit.Test
//import org.koin.android.ext.koin.androidContext
//import org.koin.androidx.workmanager.factory.KoinWorkerFactory
//import org.koin.androidx.workmanager.koin.setupWorkManagerFactory
//import org.koin.core.KoinApplication
//import org.koin.core.context.startKoin
//import org.koin.core.definition.BeanDefinition
//import org.koin.core.error.NoScopeDefFoundException
//import org.koin.core.logger.Level
//import org.koin.core.scope.Scope
//import org.koin.test.AutoCloseKoinTest
//import org.mockito.Mockito.mock
//import kotlin.reflect.KClass
//
///**
// * @author Fabio de Matos
// * @since 21/08/2020.
// */
//class ScopeDSLExtKtTest : AutoCloseKoinTest() {
//
//    private val applicationContext = mock(Context::class.java)
//
//    private val factory = KoinWorkerFactory()
//
//    private fun createWorkParameterScope(
//        applicationContext: Context,
//        workerParameters: WorkerParameters
//    ): Scope {
//        return factory.createWorkParameterScope(applicationContext, workerParameters)
//    }
//
//    @Test
//    fun `should not load from root scope`() {
//        val koinApp = startKoin {
//            printLogger(Level.DEBUG)
//            androidContext(applicationContext)
//            setupWorkManagerFactory()
//        }
//
//        assertNull(koinApp.getDefinition(WorkerParameters::class))
//
//    }
//
//    @Test
//    fun `given not initialized should not find work manager definition`() {
//        val koinApp = startKoin {
//            printLogger(Level.DEBUG)
//        }
//
//        assertNull(koinApp.getDefinition(KoinWorkerFactory::class))
//
//    }
//
//    @Test(expected = NoScopeDefFoundException::class)
//    fun `given not initialized should throw error`() {
//        startKoin {
//            printLogger(Level.DEBUG)
//
//        }
//
//        val wp = mock(WorkerParameters::class.java)
//
//        createWorkParameterScope(applicationContext, wp)
//    }
//
//    @Test
//    fun `given scope resolve work parameters`() {
//        val koinApp = startKoin {
//            printLogger(Level.DEBUG)
//            androidContext(applicationContext)
//            setupWorkManagerFactory()
//        }
//
//        val wp = mock(WorkerParameters::class.java)
//
//        assertNotNull(wp)
//
//        val scope = createWorkParameterScope(applicationContext, wp)
//
//
//        val resolvedObject = scope.getOrNull<WorkerParameters>()
//
//        assertEquals(wp, resolvedObject)
//        assertNull(koinApp.getDefinition(WorkerParameters::class))
//
//    }
//
//    @Test
//    fun `given 2 injections don't mix them`() {
//        startKoin {
//            printLogger(Level.DEBUG)
//            androidContext(applicationContext)
//            setupWorkManagerFactory()
//        }
//
//        val wp1 = mock(WorkerParameters::class.java)
//        val wp2 = mock(WorkerParameters::class.java)
//
//        assertNotEquals(wp1, wp2)
//
//        val scope1 = createWorkParameterScope(applicationContext, wp1)
//        val scope2 = createWorkParameterScope(applicationContext, wp2)
//
//        assertNotEquals(
//            scope1.getOrNull<WorkerParameters>(),
//            scope2.getOrNull<WorkerParameters>()
//        )
//
//        assertEquals(wp1, scope1.getOrNull<WorkerParameters>())
//        assertEquals(wp2, scope2.getOrNull<WorkerParameters>())
//
//    }
//
//
//}
//
//
///**
// * Find definition
// * @param clazz
// */
//fun KoinApplication.getDefinition(clazz: KClass<*>): BeanDefinition<*>? {
//    val scopeDefinition = this.koin._scopeRegistry._rootScopeDefinition
//    return scopeDefinition!!.definitions.firstOrNull { it.`is`(clazz, null, scopeDefinition) }
//}