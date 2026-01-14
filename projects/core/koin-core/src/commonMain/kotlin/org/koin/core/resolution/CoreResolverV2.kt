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
        return resolveFromContextOrNull(scope,instanceContext) ?: throwNoDefinitionFound(instanceContext)
    }

    private fun <T> resolveFromContextOrNull(scope : Scope, instanceContext: ResolutionContext): T? {
        return resolveFromRegistry(scope, instanceContext)
            ?: resolveFromInjectedParameters(instanceContext)
            ?: resolveFromStackedParameters(scope,instanceContext)
            ?: resolveInExtensions(scope,instanceContext)
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
        var resolvedScope: Scope? = null

        val factory = scopes.firstNotNullOfOrNull { linkedScope ->
            val definition = findDefinitionInScope(linkedScope, ctx)
            if (definition != null) {
                resolvedScope = linkedScope
            }
            definition
        }

        val foundScope = resolvedScope ?: return null
        if (factory == null) return null

        val newCtx = ctx.newContextForScope(foundScope)
        if (foundScope.scopeArchetype != null && !foundScope.isRoot) {
            newCtx.scopeArchetype = foundScope.scopeArchetype
        }
        return factory.get(newCtx) as T?
    }

    private fun findDefinitionInScope(scope: Scope, ctx: ResolutionContext): InstanceFactory<*>? {
        return scope.scopeArchetype?.let { archetype ->
            _koin.instanceRegistry.resolveDefinition(ctx.clazz, ctx.qualifier, archetype)
        } ?: _koin.instanceRegistry.resolveDefinition(ctx.clazz, ctx.qualifier, scope.scopeQualifier)
    }

    private inline fun <T> resolveFromInjectedParameters(ctx: ResolutionContext): T? {
        return if (ctx.parameters == null || ctx.parameters.isEmpty()) null
        else {
            ctx.logger.debug("|- ? ${ctx.debugTag} look in injected parameters")
            ctx.parameters.getOrNull(clazz = ctx.clazz)
        }
    }

    private inline fun <T> resolveFromStackedParameters(scope: Scope, ctx: ResolutionContext): T? {
        val current = scope.parameterStack?.get()
        return if (current.isNullOrEmpty()) null
        else {
            ctx.logger.debug("|- ? ${ctx.debugTag} look in stack parameters")
            val parameters = current.firstOrNull()
            parameters?.getOrNull(ctx.clazz)
        }
    }

    private inline fun <T> throwNoDefinitionFound(ctx: ResolutionContext): T {
        val qualifierString = ctx.qualifier?.let { " and qualifier '$it'" } ?: ""
        throw NoDefinitionFoundException(
            "No definition found for type '${ctx.clazz.getFullName()}'$qualifierString on scope '${ctx.scope}'. Check your Modules configuration and add missing type and/or qualifier!",
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