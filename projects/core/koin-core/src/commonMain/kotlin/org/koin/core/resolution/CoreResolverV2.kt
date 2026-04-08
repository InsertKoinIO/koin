/*
 * Copyright 2017-Present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.koin.core.resolution

import org.koin.core.Koin
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.error.NoDefinitionFoundException
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.ResolutionContext
import org.koin.core.scope.Scope
import org.koin.ext.getFullName


/**
 * Resolver - optimised version
 */
@KoinInternalApi
class CoreResolverV2(
    private val _koin : Koin
) : InstanceResolver {

    override val extendedResolution = arrayListOf<ResolutionExtension>()

    override fun addResolutionExtension(resolutionExtension : ResolutionExtension){
        extendedResolution += resolutionExtension
    }

    override fun <T> resolveFromContext(scope : Scope, instanceContext: ResolutionContext): T {
        return resolveFromContextOrNull(scope,instanceContext) ?: throwNoDefinitionFound(scope, instanceContext)
    }

    private fun <T> resolveFromContextOrNull(scope : Scope, instanceContext: ResolutionContext): T? {
        return resolveFromInjectedParameters(instanceContext)
            ?: resolveFromStackedParameters(scope, instanceContext)
            ?: resolveFromRegistry(scope, instanceContext)
            ?: resolveInExtensions(scope, instanceContext)
    }

    private fun <T> resolveFromRegistry(
        scope: Scope,
        ctx: ResolutionContext
    ): T? {
        return resolveDirectDefinition(scope, ctx)
            ?: resolveFromScopeSource(scope, ctx)
            ?: resolveFromLinkedScopes(scope, ctx)
    }

    private fun <T> resolveDirectDefinition(scope: Scope, ctx: ResolutionContext): T? {
        val factory = _koin.instanceRegistry.resolveDefinition(ctx.clazz, ctx.qualifier, scope.scopeQualifier)
            ?: resolveFromScopeArchetype(scope, ctx)
        return factory?.get(ctx) as T?
    }

    private fun resolveFromScopeArchetype(scope: Scope, ctx: ResolutionContext): InstanceFactory<*>? {
        if (scope.isRoot) return null
        return scope.scopeArchetype?.let { archetype ->
            ctx.scopeArchetype = archetype
            _koin.instanceRegistry.resolveDefinition(ctx.clazz, ctx.qualifier, archetype)
        }
    }

    private fun <T> resolveFromScopeSource(scope: Scope, ctx: ResolutionContext): T? {
        if (scope.isRoot || ctx.qualifier != null) return null
        return if (ctx.clazz.isInstance(scope.sourceValue)) scope.sourceValue as? T else null
    }

    private fun <T> resolveFromLinkedScopes(scope: Scope, ctx: ResolutionContext): T? {
        val scopes = listOf(scope) + flatten(scope.linkedScopes)

        for (linkedScope in scopes) {
            // 1. Registry on this linked scope
            val factory = findDefinitionInScope(linkedScope, ctx)
            if (factory != null) {
                // we will loose parameters from parent context
                val newCtx = ctx.newContextForScope(linkedScope)
                if (linkedScope.scopeArchetype != null && !linkedScope.isRoot) {
                    newCtx.scopeArchetype = linkedScope.scopeArchetype
                }

                // stack params for call on other Scope
                val paramStack = if (newCtx.parameters != null) {
                    newCtx.scope.onParameterOnStack(newCtx.parameters)
                } else null

                // call with scope
                val value = factory.get(newCtx) as T?

                // unstack params - reuse stack reference, avoid second ThreadLocal access
                paramStack?.let { newCtx.scope.clearParameterStack(it) }

                return value
            }

            // 2. Stacked params on this linked scope (issue #2387):
            // restores access to parameters stacked on parent scopes (e.g.
            // AndroidParametersHolder stacked on root by KoinViewModelFactory,
            // resolved from a child ViewModel scope's property initializer).
            // Skip the current scope: resolveFromStackedParameters was already
            // called for it at the top of resolveFromContextOrNull. Cost is one
            // ThreadLocal null-check per linked scope, ~free thanks to the
            // _parameterStack short-circuit.
            if (linkedScope !== scope) {
                val fromStack = resolveFromStackedParameters<T>(linkedScope, ctx)
                if (fromStack != null) return fromStack
            }
        }
        return null
    }

    private fun findDefinitionInScope(scope: Scope, ctx: ResolutionContext): InstanceFactory<*>? {
        return scope.scopeArchetype?.let { archetype ->
            _koin.instanceRegistry.resolveDefinition(ctx.clazz, ctx.qualifier, archetype)
        } ?: _koin.instanceRegistry.resolveDefinition(ctx.clazz, ctx.qualifier, scope.scopeQualifier)
    }

    private inline fun <T> resolveFromInjectedParameters(ctx: ResolutionContext): T? {
        return if (ctx.parameters == null || ctx.parameters.isEmpty()) null
        else {
//            ctx.logger.debug("|- ? ${ctx.debugTag} look in injected parameters")
            ctx.parameters.getOrNull(clazz = ctx.clazz)
        }
    }

    private inline fun <T> resolveFromStackedParameters(scope: Scope, ctx: ResolutionContext): T? {
        val stack = scope._parameterStack ?: return null
        val current = stack.get()
        return if (current.isNullOrEmpty()) null
        else {
//            ctx.logger.debug("|- ? ${ctx.debugTag} look in stack parameters")
            val parameters = current.firstOrNull()
            parameters?.getOrNull(ctx.clazz)
        }
    }

    private inline fun <T> throwNoDefinitionFound(scope: Scope, ctx: ResolutionContext): T {
        val qualifierString = ctx.qualifier?.let { " and qualifier '$it'" } ?: ""
        val scopeInfo = if (ctx.scope != scope) {
            "scope '${scope}' (resolution context scope: '${ctx.scope}')"
        } else {
            "scope '${scope}'"
        }
        val linkedScopeIds = scope.getLinkedScopeIds()
        val searchedScopes = if (linkedScopeIds.isNotEmpty()) {
            " Searched scopes: ['${scope.id}'] -> ${linkedScopeIds.map { "['$it']" }}"
        } else ""
        throw NoDefinitionFoundException(
            "No definition found for type '${ctx.clazz.getFullName()}'$qualifierString on $scopeInfo.$searchedScopes. Check or add definition for type '${ctx.clazz.getFullName()}'$qualifierString in scope '${scope.scopeQualifier}'.",
        )
    }

    private fun <T> resolveInExtensions(
        scope: Scope,
        ctx: ResolutionContext
    ) : T?{
        return extendedResolution.firstNotNullOfOrNull {
            ctx.logger.debug("|- ['${it.name}'] ?")
            it.resolve(scope,ctx) as T?
        }
    }
}