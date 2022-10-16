package org.koin.test.android.ext.koin

import android.app.Application
import android.content.Context
import io.mockk.every
import io.mockk.mockk
import java.lang.Exception
import org.junit.Assert
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.android.logger.AndroidLogger
import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import org.koin.core.registry.saveProperties

class KoinExtTest {

    @Test
    fun `GIVEN KoinApplication and Debug level WHEN call android logger THEN setup logger with level expected`() {
        Level.DEBUG.expetedFor { it.androidLogger(Level.DEBUG) }
    }

    @Test
    fun `GIVEN KoinApplication and Error level WHEN call android logger THEN setup logger with level expected`() {
        Level.ERROR.expetedFor { it.androidLogger(Level.ERROR) }
    }

    @Test
    fun `GIVEN KoinApplication and None level WHEN call android logger THEN setup logger with level expected`() {
        Level.NONE.expetedFor { it.androidLogger(Level.NONE) }
    }

    @Test
    fun `GIVEN KoinApplication and Info level WHEN call android logger THEN setup logger with level expected`() {
        Level.INFO.expetedFor { it.androidLogger(Level.INFO) }
    }

    @Test
    fun `GIVEN KoinApplication and default level WHEN call android logger THEN setup logger with level expected`() {
        Level.INFO.expetedFor { it.androidLogger() }
    }

    private fun Level.expetedFor(code: (KoinApplication) -> KoinApplication) {
        val koinApplication = code(KoinApplication.init())

        // THEN
        Assert.assertTrue(koinApplication.koin.logger is AndroidLogger)
        Assert.assertEquals(this, koinApplication.koin.logger.level)
    }

    @OptIn(KoinInternalApi::class)
    @Test
    fun `GIVEN KoinApplication and Application WHEN setup androidContext() THEN result as expected`() {
        // GIVEN
        val customLogger = object : Logger(Level.INFO) {
            override fun log(level: Level, msg: MESSAGE) {
                Assert.assertTrue(Level.INFO == level)
                Assert.assertEquals("[init] declare Android Context", msg)

            }
        }
        val context = mockk<Application>(relaxed = true)
        val koinApplication = KoinApplication.init()
        koinApplication.koin.setupLogger(customLogger)

        // WHEN
        koinApplication.androidContext(context)

        // THEN
        val instances = koinApplication.koin.instanceRegistry.instances
        Assert.assertTrue(instances.isNotEmpty())
        Assert.assertTrue(instances.size == 2)

        val keys = instances.keys.toList()
        val values = instances.flatMap { it.value.beanDefinition.secondaryTypes }.distinct()
        val keyContext = keys[0]
        val keyApplication = keys[1]

        Assert.assertEquals(keyContext, "android.content.Context::_root_")
        Assert.assertEquals(keyApplication, "android.app.Application::_root_")
        Assert.assertTrue(values.contains(Context::class))
        Assert.assertTrue(values.contains(Application::class))

    }

    @OptIn(KoinInternalApi::class)
    @Test
    fun `GIVEN KoinApplication and Context WHEN setup androidContext() THEN result as expected`() {
        // GIVEN
        val customLogger = object : Logger(Level.INFO) {
            override fun log(level: Level, msg: MESSAGE) {
                Assert.assertTrue(Level.INFO == level)
                Assert.assertEquals("[init] declare Android Context", msg)

            }
        }
        val context = mockk<Context>(relaxed = true)
        val koinApplication = KoinApplication.init()
        koinApplication.koin.setupLogger(customLogger)

        // WHEN
        koinApplication.androidContext(context)

        // THEN
        val instances = koinApplication.koin.instanceRegistry.instances
        Assert.assertTrue(instances.isNotEmpty())
        Assert.assertTrue(instances.size == 1)

        val key = instances.keys.first()
        val value = instances.values.first()
        Assert.assertEquals(key, "android.content.Context::_root_")
        Assert.assertTrue(value.beanDefinition.primaryType == Context::class)
    }

    @OptIn(KoinInternalApi::class)
    @Test
    fun `GIVEN KoinApplication WHEN call android properties THEN result as expected`() {
        // GIVEN
        val koinApplication = KoinApplication.init()
        val exception = Exception("exception")
        val customLogger = object : Logger(Level.INFO) {
            override fun log(level: Level, msg: MESSAGE) {
                // THEN
                when {
                    msg.contains("[init]") -> {
                        Assert.assertTrue(Level.INFO == level)
                        Assert.assertEquals("[init] declare Android Context", msg)
                    }
                    msg.contains("no assets") -> {
                        Assert.assertTrue(Level.INFO == level)
                        Assert.assertEquals("[Android-Properties] no assets/koin.properties file to load", msg)
                    }
                    msg.contains("loaded") -> {
                        Assert.assertTrue(Level.INFO == level)
                        Assert.assertEquals("[Android-Properties] loaded kotlin.Unit properties from assets/koin.properties", msg)
                        every { koinApplication.koin.propertyRegistry.saveProperties(any()) } throws exception
                    }
                    msg.contains("[Android-Properties] error for") -> {
                        Assert.assertTrue(Level.ERROR == level)
                        Assert.assertTrue(msg.contains("[Android-Properties] error for binding properties : "))
                        every { koinApplication.koin.get<Context>() } throws exception
                    }
                    msg.contains("[Android-Properties] error while") -> {
                        Assert.assertTrue(Level.ERROR == level)
                        Assert.assertTrue(msg.contains("[Android-Properties] error while loading properties from assets/koin.properties : "))
                    }
                }
            }
        }
        val context = mockk<Context>(relaxed = true) {
            every { assets?.list("") } returns arrayOf("koin.properties")
        }
        koinApplication.koin.setupLogger(customLogger)
        koinApplication.androidContext(context)

        // WHEN
        koinApplication.androidFileProperties(koinPropertyFile = "koin.properties")
        koinApplication.androidFileProperties(koinPropertyFile = "koin.properties")
        koinApplication.androidFileProperties()
    }


}
