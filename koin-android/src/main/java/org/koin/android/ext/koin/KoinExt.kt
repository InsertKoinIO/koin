package org.koin.android.ext.koin

import android.app.Application
import android.content.Context
import org.koin.Koin
import org.koin.core.bean.BeanDefinition
import org.koin.core.scope.Scope
import java.util.*

/**
 * Start Koin Android features
 * - assets/koin.properties loading
 * - application/context binding
 */
infix fun Koin.with(application: Application): Koin {
    Koin.logger.log("[init] Load Android features")
    init(application)
    return this
}

/**
 * init android Application dependency in Koin context
 * @param application - Android Application instance
 */
fun Koin.init(application: Application): Koin {
    Koin.logger.log("[init] ~ added Android application bean reference")
    // provide Application defintion
    beanRegistry.declare(
        BeanDefinition(
            clazz = Application::class,
            types = listOf(Context::class),
            definition = { application }), Scope.root()
    )
    return this
}

/**
 * Load properties file from Assets
 * @param application
 * @param koinPropertyFile
 */
fun Koin.bindAndroidProperties(
    application: Application,
    koinPropertyFile: String = "koin.properties"
): Koin {
    val koinProperties = Properties()
    try {
        val hasFile = application.assets.list("").contains(koinPropertyFile)
        if (hasFile) {
            try {
                application.assets.open(koinPropertyFile).use { koinProperties.load(it) }
                val nb = propertyResolver.import(koinProperties)
                Koin.logger.log("[Android-Properties] loaded $nb properties from assets/koin.properties")
            } catch (e: Exception) {
                Koin.logger.log("[Android-Properties] error for binding properties : $e")
            }
        } else {
            Koin.logger.log("[Android-Properties] no assets/koin.properties file to load")
        }
    } catch (e: Exception) {
        Koin.logger.err("[Android-Properties] error while loading properties from assets/koin.properties : $e")
    }
    return this
}
