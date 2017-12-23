package org.koin.sampleapp.view

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.sampleapp.di.DatasourceProperties.SERVER_URL
import org.koin.sampleapp.di.testRemoteDatasource
import org.koin.sampleapp.util.rx.SchedulerProvider
import org.koin.sampleapp.util.rx.with
import org.koin.sampleapp.view.main.MainViewModel
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest

class MainViewModelTest : KoinTest {

    val mainViewModel: MainViewModel by inject()
    val scheduler: SchedulerProvider by inject()

    @Before
    fun before() {
        startKoin(testRemoteDatasource, properties = mapOf(SERVER_URL to "https://my-weather-api.herokuapp.com/"))
    }

    @After
    fun after() {
        closeKoin()
    }

    @Test
    fun testGetWeather() {
        val test = mainViewModel.searchWeather("Paris")
                .with(scheduler)
                .test()

        test.awaitTerminalEvent()
        test.assertComplete()
    }
}