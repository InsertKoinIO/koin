package org.koin.test.android

import org.koin.android.AndroidModule

/**
 * Some tests classes
 */

class SampleModule : AndroidModule() {
    override fun context() =
            applicationContext {
                provide { AndroidComponent(get()) }
            }
}


class ComplexModule : AndroidModule() {
    override fun context() =
            applicationContext {
                provide { AndroidComponent(get()) }
                provide { OtherService(get(), getProperty("url")) }
            }
}


class ComplexPropertyModule : AndroidModule() {
    override fun context() =
            applicationContext {
                provide { AndroidComponent(get()) }
                provide { OtherService(get(), getProperty(OtherService.URL)) }
            }
}

class ActivityModule : AndroidModule() {
    override fun context() =
            applicationContext {
                context(CTX_ACTIVITY_MODULE) {
                    provide { OtherService(get(), getProperty("url")) }
                }
            }

    companion object {
        val CTX_ACTIVITY_MODULE = "ActivityModule"
    }
}
