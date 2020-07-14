package org.koin.test

import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import org.koin.test.mock.declare

/**
 * Tests for [KoinTestRule]. [FixMethodOrder] is used intentionally because it is important to run
 * tests in the particular order for testing proper clean up of Koin after the first test.
 *
 * @author Jan Mottl
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class KoinTestRuleTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create()

    @Test
    fun `A - can declare dependency because Koin was started`() {
        declare { TestType() }
    }

    @Test
    fun `B - can declare dependency again because Koin was stopped after first test`() {
        declare { TestType() }
    }
}

/**
 * Just a helper class for testing declaration of type in Koin.
 *
 * @author Jan Mottl
 */
private class TestType