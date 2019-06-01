package org.koin.android.viewmodel

import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.android.viewmodel.dsl.isViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Kind
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.reflect.KClass

class ViewModelDSLTest {

    @Test
    fun `definition should be a viewmodel`() {
        val koinApp = koinApplication {
            printLogger(Level.DEBUG)
            modules(module {
                viewModel { MyViewModel() }
            })
        }

        val viemodelDefinition = koinApp.getDefinition(MyViewModel::class)!!
        assertTrue(viemodelDefinition.isViewModel())
        assertTrue(viemodelDefinition.kind == Kind.Factory)
    }

    @Test
    fun `definition should not be a viewmodel`() {
        val koinApp = koinApplication {
            printLogger(Level.DEBUG)
            modules(module {
                single { MyComponent() }
            })
        }

        val otherDefinition = koinApp.getDefinition(MyComponent::class)!!
        assertTrue(!otherDefinition.isViewModel())
    }
}

/**
 * Find definition
 * @param clazz
 */
fun KoinApplication.getDefinition(clazz: KClass<*>): BeanDefinition<*>? {
    return this.koin.rootScope.beanRegistry.getDefinition(clazz)
}