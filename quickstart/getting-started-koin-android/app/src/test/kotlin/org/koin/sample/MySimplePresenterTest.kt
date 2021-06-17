package org.koin.sample

import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.sample.view.simple.MySimplePresenter
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class MySimplePresenterTest : KoinTest {


    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger(Level.DEBUG)
        modules(appModule)
    }

    val presenter: MySimplePresenter by inject()

    @After
    fun after(){
        stopKoin()
    }

    @Test
    fun `say hello`() {
        // reuse the lazy injected presenter
        assertTrue(presenter.sayHello() == "Hello Koin from MySimplePresenter")
    }

    @Test
    fun `say hello with mock`() {
        val mock = declareMock<HelloRepository>()

        // retrieve actual presenter (injected with mock)
        val presenter = get<MySimplePresenter>()
        presenter.sayHello()

        verify(mock, times(1)).giveHello()
    }
}