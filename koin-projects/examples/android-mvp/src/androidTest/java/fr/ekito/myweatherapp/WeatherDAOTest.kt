//package fr.ekito.myweatherapp
//
//import android.support.test.runner.AndroidJUnit4
//import fr.ekito.myweatherapp.data.datasource.room.WeatherDAO
//import fr.ekito.myweatherapp.data.datasource.room.WeatherDatabase
//import fr.ekito.myweatherapp.data.datasource.room.WeatherEntity
//import fr.ekito.myweatherapp.data.datasource.webservice.WeatherWebDatasource
//import fr.ekito.myweatherapp.data.datasource.webservice.json.geocode.getLocation
//import fr.ekito.myweatherapp.data.datasource.webservice.json.weather.getDailyForecasts
//import junit.framework.Assert
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.koin.standalone.StandAloneContext.closeKoin
//import org.koin.standalone.StandAloneContext.loadKoinModules
//import org.koin.standalone.inject
//import org.koin.test.KoinTest
//import java.util.*
//
///**
// * WeatherDAOTest is a KoinTest: can inject Koin components from actual runtime
// */
//@RunWith(AndroidJUnit4::class)
//class WeatherDAOTest : KoinTest {
//
//    /*
//     * Inject needed components from Koin
//     */
//    val weatherDatabase: WeatherDatabase by inject()
//    val weatherWebDatasource: WeatherWebDatasource by inject()
//    val weatherDAO: WeatherDAO by inject()
//
//    @Before()
//    fun before() {
//        // Replace default WeatherDatabase definition with in-memory definition
//        loadKoinModules(roomTestModule)
//    }
//
//    @After
//    fun after() {
//        weatherDatabase.close()
//        closeKoin()
//    }
//
//    @Test
//    fun testSave() {
//        val location = "Paris"
//
//        val now = Date()
//        val entities = getWeatherAsEntities(location, now)
//
//        weatherDAO.saveAll(entities)
//        val ids = entities.map { it.id }
//
//        val requestedEntities = ids.map { weatherDAO.findWeatherById(it).blockingGet() }
//
//        Assert.assertEquals(entities, requestedEntities)
//    }
//
//    @Test
//    fun testFindAllBy() {
//        val locationParis = "Paris"
//        val dateParis = Date()
//        val weatherParis = getWeatherAsEntities(locationParis, dateParis)
//        weatherDAO.saveAll(weatherParis)
//
//        val locationTlse = "Toulouse"
//        val dateToulouse = Date()
//        val weatherToulouse = getWeatherAsEntities(locationTlse, dateToulouse)
//        weatherDAO.saveAll(weatherToulouse)
//
//        val resultList = weatherDAO.findAllBy(locationTlse, dateToulouse).blockingGet()
//
//        Assert.assertEquals(weatherToulouse, resultList)
//    }
//
//    @Test
//    fun testFindLatest() {
//        val locationParis = "Paris"
//        val dateParis = Date()
//        val weatherParis = getWeatherAsEntities(locationParis, dateParis)
//        weatherDAO.saveAll(weatherParis)
//
//        val locationBerlin = "Berlin"
//        val dateBerlin = Date()
//        val weatherBerlin = getWeatherAsEntities(locationBerlin, dateBerlin)
//        weatherDAO.saveAll(weatherBerlin)
//
//        val locationTlse = "Toulouse"
//        val dateToulouse = Date()
//        val weatherToulouse = getWeatherAsEntities(locationTlse, dateToulouse)
//        weatherDAO.saveAll(weatherToulouse)
//
//        val result: WeatherEntity = weatherDAO.findLatestWeather().blockingGet().first()
//        val resultList = weatherDAO.findAllBy(result.location, result.date).blockingGet()
//
//        Assert.assertEquals(weatherToulouse, resultList)
//    }
//
//    private fun getWeatherAsEntities(
//        locationParis: String,
//        dateParis: Date
//    ): List<WeatherEntity> {
//        return weatherWebDatasource.geocode(locationParis)
//            .map { it.getLocation() }
//            .flatMap { weatherWebDatasource.weather(it.lat, it.lng, "EN") }
//            .map { it.getDailyForecasts(locationParis) }
//            .map { list -> list.map { WeatherEntity.from(it, dateParis) } }
//            .blockingGet()
//    }
//}