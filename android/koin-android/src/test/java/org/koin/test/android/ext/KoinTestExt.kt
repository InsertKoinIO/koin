package org.koin.test.android.ext

import android.app.Application
import org.koin.Koin
import org.koin.android.ext.koin.init
import org.koin.android.module.AndroidModule
import org.koin.standalone.StandAloneContext
import org.koin.test.KoinTest

fun KoinTest.startAndroidContext(application: Application, modules: List<AndroidModule>) {
    StandAloneContext.koinContext = Koin().init(application).build(modules)
}