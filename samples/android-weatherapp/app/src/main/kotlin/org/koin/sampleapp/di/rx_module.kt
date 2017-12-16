package org.koin.sampleapp.di

import org.koin.dsl.module.applicationContext
import org.koin.sampleapp.util.rx.ApplicationSchedulerProvider
import org.koin.sampleapp.util.rx.SchedulerProvider

val RxModule = applicationContext {
    // provided components
    bean { ApplicationSchedulerProvider() as SchedulerProvider }
}