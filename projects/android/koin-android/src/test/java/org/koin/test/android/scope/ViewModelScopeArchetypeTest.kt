package org.koin.test.android.scope

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.ActivityRetainedScopeArchetype
import org.koin.androidx.scope.ActivityScopeArchetype
import org.koin.androidx.scope.FragmentScopeArchetype
import org.koin.androidx.scope.activityRetainedScope
import org.koin.androidx.scope.activityScope
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.androidx.scope.dsl.activityScope
import org.koin.androidx.scope.dsl.fragmentScope
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.getScopeId
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.viewModel
import org.koin.core.option.viewModelScopeFactory
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import org.koin.test.android.scope.ScopeArchetypeDSLTest.MyFactoryClass
import org.koin.test.android.scope.ScopeArchetypeDSLTest.MyScopedClass
import org.koin.viewmodel.scope.viewModelScope
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

class ScopedVM(){
    val id : String = UUID.randomUUID().toString()
}
class FakeVM(val s : ScopedVM) : ViewModel()
class FakeVMActivity : ComponentActivity() {

    val fakeVM : FakeVM by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun useViewModel(){
        println("fakeVM :$fakeVM")
    }
}

@RunWith(RobolectricTestRunner::class)
class ViewModelScopeArchetypeTest {

    @OptIn(KoinExperimentalAPI::class)
    @Before
    fun setup() {
        startKoin {
            printLogger(Level.DEBUG)

            options(
                viewModelScopeFactory()
            )
        }
    }

    @After
    fun stop() {
        stopKoin()
    }

    @Test
    fun `viewModelScope resolves`() {
        val koin = KoinPlatform.getKoin()
        val module = module {
            viewModel { FakeVM(get()) }
            viewModelScope {
                scoped { ScopedVM() }
            }
        }
        koin.loadModules(listOf(module))

        val controller: ActivityController<FakeVMActivity> = Robolectric.buildActivity(FakeVMActivity::class.java)
        val activity: FakeVMActivity? = controller.get()
        assertNotNull(activity)
        controller.create().start().resume()

        activity.useViewModel()

        assertNotNull(activity.fakeVM)
    }
}
