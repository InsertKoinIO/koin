//package org.koin.ext
//
//import org.koin.core.Koin
//
///**
// * Koin extension functions
// *
// * Non-essential helper functions
// *
// * @author Edward Zhou
// */
//
///**
// * Retrieve an int property
// * @param key
// * @param defaultValue
// */
//fun Koin.getIntProperty(key: String, defaultValue: Int): Int {
//    return getIntFromStringProperty(key) ?: defaultValue
//}
//
///**
// * Retrieve an int property
// * @param key
// */
//fun Koin.getIntFromStringProperty(key: String): Int? {
//    return getProperty<String>(key)?.toIntOrNull()
//}
//
///**
// * Save an int property
// * @param key
// * @param value
// */
//fun Koin.setIntProperty(key: String, value: Int) {
//    setProperty(key, value.toString())
//}
//
///**
// * Retrieve a float property
// * @param key
// * @param defaultValue
// */
//fun Koin.getFloatProperty(key: String, defaultValue: Float): Float {
//    return getFloatFrontStringProperty(key) ?: defaultValue
//}
//
///**
// * Retrieve a float property
// * @param key
// */
//fun Koin.getFloatFrontStringProperty(key: String): Float? {
//    return getProperty<String>(key)?.toFloatOrNull()
//}
//
///**
// * Save a float property
// * @param key
// * @param value
// */
//fun Koin.setFloatProperty(key: String, value: Float) {
//    setProperty(key, value.toString())
//}
