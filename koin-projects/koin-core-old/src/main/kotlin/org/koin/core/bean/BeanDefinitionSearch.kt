package org.koin.core.bean

import org.koin.core.scope.Scope
import org.koin.core.scope.isVisibleToScope
import org.koin.dsl.definition.BeanDefinition
import org.koin.error.NotVisibleException


typealias BeanDefinitionSearch = () -> BeanDefinitionSearchResult
typealias BeanDefinitionSearchResult = List<BeanDefinition<*>>


fun BeanDefinitionSearchResult.isVisibleToLastInStack(
    lastInStack: BeanDefinition<*>?
): BeanDefinitionSearchResult {
    return if (lastInStack == null) this
    else {
        val visibleToStack = this.filter { lastInStack.isVisible(it) }
        if (visibleToStack.isEmpty() && this.isNotEmpty()) throw NotVisibleException("Definition is not visible from last definition : $lastInStack")
        visibleToStack
    }
}

fun BeanDefinitionSearchResult.isVisibleToScope(
    scope: Scope?
): BeanDefinitionSearchResult {
    return if (scope == null) this
    else {
        val visibleToScope = this.filter { it.isVisibleToScope(scope) }
        if (visibleToScope.isEmpty() && this.isNotEmpty()) throw NotVisibleException("Definition is not visible from scope: $scope")
        visibleToScope
    }
}

fun <T> BeanDefinitionSearchResult.takeDefault() : BeanDefinition<T>? {
    return this.firstOrNull { it.name.isEmpty() } as BeanDefinition<T>?
}

fun <T> BeanDefinitionSearchResult.takeFirst() : BeanDefinition<T> {
    return this.first() as BeanDefinition<T>
}