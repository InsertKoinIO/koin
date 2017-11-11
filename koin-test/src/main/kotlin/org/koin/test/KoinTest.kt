package org.koin.test

import org.koin.Koin
import org.koin.KoinContext
import org.koin.dsl.module.Module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext

interface KoinTest : KoinComponent

inline fun <reified T> KoinTest.get(name: String = "") = (StandAloneContext.koinContext as KoinContext).get<T>(name)

inline fun <reified T> KoinTest.getProperty(name: String = "") = (StandAloneContext.koinContext as KoinContext).getProperty<T>(name)

inline fun <reified T> KoinTest.getProperty(name: String = "", defaultVale: T) = (StandAloneContext.koinContext as KoinContext).getProperty(name, defaultVale)


/**
 * Dry Run Starter
 */
fun KoinTest.dryRun(modules: List<Module>) = Koin().build(modules).dryRun()