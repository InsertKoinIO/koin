/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.koin.java

import org.junit.After
import org.junit.Before
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * @author @fredy-mederos
 */
open class KoinJavaComponentTest {

    @Before
    fun before() {
        startKoin {
            properties(mapOf("PrefixProp" to "_", "SeparatorProp" to "|"))
            modules(
                module {
                    single(named("db")) { LocalDbImplementation() as DataSource }
                    single(named("api")) { RemoteApiImplementation() as DataSource }
                    single { (separator: String) -> DataConverter(separator) }
                },
            )
        }
    }

    @After
    fun after() {
        stopKoin()
    }

    // Getting java.lang.NoClassDefFoundError: org/koin/java/DataFetcher
    // https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#java-support-in-jvm-targets
    /*@Test
    fun `inject components in java DataFetcher class test`() {
        val dataFetcherInstance = DataFetcher()
        assertEquals("_data from db|data from api", dataFetcherInstance.allDataConverted)
    }*/
}

interface DataSource {
    fun getData(): String
}

class LocalDbImplementation : DataSource {
    override fun getData(): String {
        // Obtaining data from db...
        return "data from db"
    }
}

class RemoteApiImplementation : DataSource {
    override fun getData(): String {
        // Obtaining data from api...
        return "data from api"
    }
}

class DataConverter(private val separator: String) {
    fun convert(vararg data: String): String {
        return data.joinToString(separator)
    }
}
