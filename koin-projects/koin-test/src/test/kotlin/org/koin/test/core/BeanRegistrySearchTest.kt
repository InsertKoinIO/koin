package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.core.bean.BeanRegistry
import org.koin.dsl.definition.BeanDefinition
import org.koin.dsl.definition.Kind

class BeanRegistrySearchTest {
    private val beanRegistry = BeanRegistry()

    interface InterfaceComponent
    class ComponentA : InterfaceComponent
    class ComponentB : InterfaceComponent

    @Test
    fun `search by class`() {
        val definitionA = BeanDefinition(
            clazz = ComponentA::class,
            definition = { ComponentA() },
            kind = Kind.Single
        )
        val definitionB = BeanDefinition(
            clazz = ComponentB::class,
            definition = { ComponentB() },
            kind = Kind.Single
        )
        beanRegistry.declare(definitionA)
        beanRegistry.declare(definitionB)

        val candidates = beanRegistry.searchByClass(ComponentA::class)

        Assert.assertEquals(listOf(definitionA), candidates)
    }

    @Test
    fun `search by interface`() {
        val definitionA = BeanDefinition(
            clazz = InterfaceComponent::class,
            definition = { ComponentA() },
            kind = Kind.Single
        )
        val definitionB = BeanDefinition(
            allowOverride = true,
            clazz = ComponentB::class,
            definition = { ComponentB() },
            kind = Kind.Single
        )
        beanRegistry.declare(definitionA)
        beanRegistry.declare(definitionB)

        val candidates = beanRegistry.searchByClass(InterfaceComponent::class)

        Assert.assertEquals(listOf(definitionA), candidates)
    }

    @Test
    fun `search by interface with filter`() {
        val definitionA = BeanDefinition(
            clazz = InterfaceComponent::class,
            definition = { ComponentA() },
            kind = Kind.Single
        )
        val definitionB = BeanDefinition(
            clazz = ComponentB::class,
            types = listOf(InterfaceComponent::class),
            definition = { ComponentB() },
            kind = Kind.Factory
        )
        beanRegistry.declare(definitionA)
        beanRegistry.declare(definitionB)

        val candidates = beanRegistry.searchByClass(InterfaceComponent::class) {
            it.kind == Kind.Single
        }

        Assert.assertEquals(listOf(definitionA), candidates)
    }

    @Test
    fun `search by name and class`() {
        val definitionA = BeanDefinition(
            clazz = ComponentA::class,
            definition = { ComponentA() },
            kind = Kind.Single,
            name = "A"
        )
        val definitionB = BeanDefinition(
            clazz = ComponentA::class,
            definition = { ComponentA() },
            kind = Kind.Single,
            name = "B"
        )
        beanRegistry.declare(definitionA)
        beanRegistry.declare(definitionB)

        val candidates = beanRegistry.searchByNameAndClass("A", ComponentA::class)

        Assert.assertEquals(listOf(definitionA), candidates)
    }

    @Test
    fun `search by name and interface`() {
        val definitionA = BeanDefinition(
            clazz = InterfaceComponent::class,
            definition = { ComponentA() },
            kind = Kind.Single,
            name = "A"
        )
        val definitionB = BeanDefinition(
            clazz = InterfaceComponent::class,
            definition = { ComponentB() },
            kind = Kind.Single,
            name = "B"
        )
        beanRegistry.declare(definitionA)
        beanRegistry.declare(definitionB)

        val candidates = beanRegistry.searchByNameAndClass("A", InterfaceComponent::class)

        Assert.assertEquals(listOf(definitionA), candidates)
    }

    @Test
    fun `search by name and interface with filter`() {
        val definitionA = BeanDefinition(
            clazz = InterfaceComponent::class,
            definition = { ComponentA() },
            kind = Kind.Single,
            name = "A"
        )
        val definitionB = BeanDefinition(
            clazz = ComponentB::class,
            definition = { ComponentB() },
            types = listOf(InterfaceComponent::class),
            kind = Kind.Factory,
            name = "A"
        )
        beanRegistry.declare(definitionA)
        beanRegistry.declare(definitionB)

        val candidates = beanRegistry.searchByNameAndClass("A", InterfaceComponent::class) {
            it.kind == Kind.Single
        }

        Assert.assertEquals(listOf(definitionA), candidates)
    }
}
