package org.koin.standalone

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.AutoCloseKoinTest

/**
 * @author @fredy-mederos
 */
open class KoinJavaComponentTest : AutoCloseKoinTest() {

    @Before
    fun before() {
        startKoin(
            listOf(applicationContext {
                bean("db") { LocalDbImplementation() as DataSource }
                bean("api") { RemoteApiImplementation() as DataSource }
                bean { params -> DataConverter(params["separator"]) }
            }),
            properties = mapOf("PrefixProp" to "_", "SeparatorProp" to "|")
        )
    }

    @Test
    fun `inject components in java DataFetcher class test`() {
        val dataFetcherInstance = DataFetcher()
        assertEquals("_data from db|data from api", dataFetcherInstance.allDataConverted)
    }
}

interface DataSource {
    fun getData(): String
}

class LocalDbImplementation : DataSource {
    override fun getData(): String {
        //Obtaining data from db...
        return "data from db"
    }
}

class RemoteApiImplementation : DataSource {
    override fun getData(): String {
        //Obtaining data from api...
        return "data from api"
    }
}

class DataConverter(private val separator: String) {
    fun convert(vararg data: String): String {
        return data.joinToString(separator)
    }
}