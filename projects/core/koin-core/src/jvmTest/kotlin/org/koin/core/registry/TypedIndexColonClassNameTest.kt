package org.koin.core.registry

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Kind
import org.koin.core.definition.indexKey
import org.koin.core.instance.SingleInstanceFactory
import org.koin.dsl.koinApplication
import org.koin.ext.getFullName
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

interface ColonXType

// Kotlin/JS (2.1+) accepts class `a:b` and reports simpleName "a:b"; on the JVM a class named with ':' is legal
// bytecode, so this redefines ColonXType under the name Colon:Type to get a KClass whose full name holds a ':'.
@OptIn(KoinInternalApi::class)
class TypedIndexColonClassNameTest {

    private fun colonClass(): KClass<*> {
        val original = "org/koin/core/registry/ColonXType"
        val renamed = "org/koin/core/registry/Colon:Type"
        val bytes = ColonXType::class.java.getResourceAsStream("ColonXType.class")!!.readBytes()
        val from = original.toByteArray()
        val to = renamed.toByteArray()
        var i = 0
        while (i <= bytes.size - from.size) {
            if ((from.indices).all { bytes[i + it] == from[it] }) { to.copyInto(bytes, i); i += from.size } else i++
        }
        val loader = object : ClassLoader(ColonXType::class.java.classLoader) {
            fun define(): Class<*> = defineClass("org.koin.core.registry.Colon:Type", bytes, 0, bytes.size)
        }
        return loader.define().kotlin
    }

    @Test
    fun override_of_a_class_whose_name_contains_a_colon_is_visible() {
        val colon = colonClass()
        assertEquals("org.koin.core.registry.Colon:Type", colon.getFullName())
        val koin = koinApplication { }.koin
        val root = koin.scopeRegistry.rootScope.scopeQualifier
        val key = indexKey(colon, null, root)
        val first = SingleInstanceFactory(BeanDefinition(root, colon, null, { "first" }, Kind.Singleton))
        koin.instanceRegistry.saveMapping(true, key, first)
        assertSame(first, koin.instanceRegistry.resolveDefinition(colon, null, root))

        val second = SingleInstanceFactory(BeanDefinition(root, colon, null, { "second" }, Kind.Singleton))
        koin.instanceRegistry.saveMapping(true, key, second)

        assertSame(koin.instanceRegistry.instances[key], second)
        assertSame(second, koin.instanceRegistry.resolveDefinition(colon, null, root), "typed cache served the replaced factory")
    }

    @Test
    fun unload_of_a_class_whose_name_contains_a_colon_evicts_it() {
        val colon = colonClass()
        val koin = koinApplication { }.koin
        val root = koin.scopeRegistry.rootScope.scopeQualifier
        val key = indexKey(colon, null, root)
        val module = org.koin.core.module.Module()
        val factory = SingleInstanceFactory(BeanDefinition(root, colon, null, { "only" }, Kind.Singleton))
        module.saveMapping(key, factory)
        koin.loadModules(listOf(module))
        assertSame(factory, koin.instanceRegistry.resolveDefinition(colon, null, root))

        koin.unloadModules(listOf(module))

        assertNull(koin.instanceRegistry.instances[key])
        assertNull(koin.instanceRegistry.resolveDefinition(colon, null, root), "typed cache survived unload")
    }
}
