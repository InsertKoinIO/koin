package org.koin.ktor.di

//import io.ktor.server.plugins.di.DependencyKey
//import io.ktor.server.plugins.di.DependencyMap
//import org.koin.core.Koin
//import org.koin.core.qualifier.Qualifier
//import org.koin.core.qualifier.named
//
///**
// * Provide Ktor DI Support for Koin to allow reverse resolution from dependencies { } expression
// *
// * @param koin - Koin instance
// */
//TODO Ktor 3.2
//class KoinDependencyMap(val koin: Koin): DependencyMap {
//    override fun contains(key: DependencyKey): Boolean {
//        return key.qualifier !is Qualifier && koin.getOrNull<Any>(key.type.type, key.toQualifier()) == null
//    }
//    override fun <T> get(key: DependencyKey): T {
//        return koin.get(key.type.type, key.toQualifier())
//    }
//}
//
//private fun DependencyKey.toQualifier(): Qualifier? {
//    return name?.let(::named)
//}