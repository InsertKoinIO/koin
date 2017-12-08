package org.koin.sampleapp.di

import org.koin.dsl.module.applicationContext
import org.koin.sampleapp.util.TestSchedulerProvider
import org.koin.sampleapp.util.rx.SchedulerProvider

val RxTestModule = applicationContext {
    // provided components
    provide { TestSchedulerProvider() as SchedulerProvider }
}