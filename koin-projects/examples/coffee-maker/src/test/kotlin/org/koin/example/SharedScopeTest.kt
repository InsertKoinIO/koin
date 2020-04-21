package org.koin.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope
import org.koin.ext.scope
import org.koin.java.KoinJavaComponent.getKoin
import org.koin.test.KoinTestRule

class SharedScopeTest : MockitoTest() {
    companion object {
        const val commonScopeId = "common_scope_id"
    }
    class CommonInteractor

    abstract class Presenter(dep: Any) {
        private val commonScope: Scope = getKoin().getOrCreateScope(commonScopeId, named(commonScopeId))
        val interactor: CommonInteractor = commonScope.get()
        private var view: View? = null
        init {
            println("Presenter created with dependency: $dep")
        }
        fun attach(view: View) {
            this@Presenter.view = view
        }
        fun detach() {
            commonScope.close()
            view?.scope?.close()
            view = null
        }
    }
    interface View {
        val presenter: Presenter
    }

    class HeaterPresenter(heater: Heater): Presenter(heater)
    class HeaterFragment: View {
        override val presenter by getOrCreateScope().inject<HeaterPresenter>()
    }
    class PumpPresenter(pump: Pump): Presenter(pump)
    class PumpFragment: View {
        override val presenter by getOrCreateScope().inject<PumpPresenter>()
    }

    private val testModule = module {
        single<Heater> { ElectricHeater() }
        single<Pump> { Thermosiphon(get()) }
        scope(named(commonScopeId)) { scoped { CommonInteractor() } }
        scope<HeaterFragment> { scoped { HeaterPresenter(get()) } }
        scope<PumpFragment> { scoped { PumpPresenter(get()) } }
    }

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger(Level.DEBUG)
        modules(testModule)
    }

    @Test
    fun `recreate common element when hasn't simultaneously open screens`() {
        val heaterView = HeaterFragment()
        heaterView.presenter.apply {
            attach(heaterView)
            detach()
        }
        val pumpView = PumpFragment()
        pumpView.presenter.apply {
            attach(pumpView)
            detach()
        }
        assertNotEquals(heaterView.presenter.interactor, pumpView.presenter.interactor)
    }

    @Test
    fun `reuse common element when screens open simultaneously`() {
        val heaterView = HeaterFragment()
        heaterView.presenter.attach(heaterView)
        val pumpView = PumpFragment()
        pumpView.presenter.apply {
            attach(pumpView)
            detach()
        }
        heaterView.presenter.detach()
        assertEquals(heaterView.presenter.interactor, pumpView.presenter.interactor)
    }
}