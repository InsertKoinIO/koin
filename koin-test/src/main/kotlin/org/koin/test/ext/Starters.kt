package org.koin.test.ext

import org.koin.Koin
import org.koin.dsl.module.Module
import org.koin.test.KoinTest

/**
 * Dry Run Starter
 */
fun KoinTest.dryRun(modules: List<Module>) = Koin().build(modules).dryRun()