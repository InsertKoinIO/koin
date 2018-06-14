package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

class SetOfBeansTest : AutoCloseKoinTest() {

    val CliAppModule = applicationContext {
        bean("command1") { Command1() as Command }
        bean("command2") { Command2() as Command }

        bean { CliApp(getAll()) }

        bean { HooksHolder(getAll()) }
    }

    interface Command
    class Command1 : Command
    class Command2 : Command

    class CliApp(val commands: Set<Command>)

    interface Hook
    class HooksHolder(val hooks: Set<Hook>)

    @Test
    fun `should resolve set of beans dependency`() {
        startKoin(listOf(CliAppModule))

        val cliApp = get<CliApp>()

        Assert.assertNotNull(cliApp.commands)
        Assert.assertEquals(2, cliApp.commands.size)
        Assert.assertNotNull(cliApp.commands.firstOrNull { it is Command1 })
        Assert.assertNotNull(cliApp.commands.firstOrNull { it is Command2 })

        assertDefinitions(4)
        assertRemainingInstances(3)
        assertContexts(1)
    }

    @Test
    fun `get empty set of beans dependency`() {
        startKoin(listOf(CliAppModule))

        val hooksHolder = get<HooksHolder>()

        Assert.assertNotNull(hooksHolder.hooks)
        Assert.assertEquals(0, hooksHolder.hooks.size)

        assertDefinitions(4)
        assertRemainingInstances(1)
        assertContexts(1)
    }
}