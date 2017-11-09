package org.koin.android.ext.koin

import android.app.Application
import org.koin.Koin
import org.koin.KoinContext
import org.koin.core.bean.BeanDefinition
import org.koin.core.scope.Scope

/**
 * init android Application dependency in Koin context
 * @param application - Android Application instance
 */
fun Koin.init(application: Application): Koin {
    // provide Application defintion
    beanRegistry.declare(BeanDefinition(clazz = Application::class, definition = { application }), Scope.root())
    return this
}

/**
 * Bind an Android String to Koin property
 * @param id - Android resource String id
 * @param key - Koin property key
 */
fun KoinContext.bindString(id: Int, key: String) {
    setProperty(key, get<Application>().getString(id))
}

/**
 * Bind an Android Integer to Koin property
 * @param id - Android resource Int id
 * @param key - Koin property key
 */
fun KoinContext.bindInt(id: Int, key: String) {
    setProperty(key, get<Application>().resources.getInteger(id))
}

/**
 * Bind an Android Boolean to Koin property
 * @param id - Android resource Boolean id
 * @param key - Koin property key
 */
fun KoinContext.bindBool(id: Int, key: String) {
    setProperty(key, get<Application>().resources.getBoolean(id))
}