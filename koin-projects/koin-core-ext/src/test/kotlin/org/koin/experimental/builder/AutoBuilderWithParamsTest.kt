package org.koin.experimental.builder

import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest

/**
 * @author @fredy-mederos
 */
class AutoBuilderWithParamsTest : AutoCloseKoinTest() {

    class ComponentA(val bool: Boolean)

    class ComponentB(
        val text: String,
        val a: ComponentA
    )

    /**
     * ComponentC contains a lot of fields, some of them will be included in params, others will be obtained from definitions
     */
    class ComponentC(
        val text: String,
        val text2: String,
        val text3: String,
        val n: Int,
        val a: ComponentA,
        val b: ComponentB,
        val bool: Boolean,
        val d: ComponentD,
        val l: List<ComponentA>
    )

    interface ComponentD

    @Test
    fun `should resolve params and build`() {
        startKoin(listOf(module {
            single<ComponentA> { create(it) }
            single<ComponentB> { create(it) }
            single<ComponentC> { create(it) }
            single { "TEXT" }
        }))

        //Getting componentC and passing some parameters. In parameters of the same type, order matters.
        val c = get<ComponentC> {
            parametersOf(
                1,
                "text",
                true,
                "text2",
                listOf(ComponentA(false)),
                object : ComponentD {})
        }

        //Asserting all componentC fields values
        assertEquals("text", c.text)
        assertEquals("text2", c.text2)
        //only two strings where included in params, the next strings are obtained from definitions
        assertEquals("TEXT", c.text3)
        assertEquals(1, c.n)
        assertEquals(true, c.bool)
        assertEquals(1, c.l.size)

        //Asserting componentA and componentB when created with componentC params
        assertEquals("text", c.b.text)
        assertEquals(true, c.a.bool)
    }
}