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
import org.koin.core.instance.ResolutionContext
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import org.koin.ext.getFullName

/**
 * ResolutionExtension offers a way to extend Koin capacity to resolve instance in external systems
 * For example it allows to extend Koin to Ktor's internal DI, and make your Koin definition benefits from Ktor DI declared components
 *
 * Initially extracted from Scope, to help externalise resolution engine and extensions.
 *
 * Each extension has
 * - a name, to help display resolution debugs
 * - implement fun resolve(scope : Scope, instanceContext: ResolutionContext)
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
interface ResolutionExtension {
    /**
     * Extension Name
     */
    val name : String

    /**
     * Resolve function for given scope and ResolutionContext
     * @param scope
     * @param instanceContext
     */
    fun resolve(scope : Scope, instanceContext: ResolutionContext) : Any?
}

/**
 *
 */
@KoinInternalApi
class CoreResolver(
    private val _koin : Koin
) : InstanceResolver {

    internal val extendedResolution = arrayListOf<ResolutionExtension>()

    fun addResolutionExtension(resolutionExtension : ResolutionExtension){
        extendedResolution += resolutionExtension
    }

    override fun <T> resolveFromContext(scope : Scope, instanceContext: ResolutionContext): T {
        return resolveFromContextOrNull(scope,instanceContext) ?: throwNoDefinitionFound(instanceContext)
    }

    private fun <T> resolveFromContextOrNull(scope : Scope, instanceContext: ResolutionContext, lookupParent : Boolean = true): T? {
        return resolveFromInjectedParameters(instanceContext)
            ?: resolveFromRegistry(scope,instanceContext)
            ?: resolveFromStackedParameters(scope,instanceContext)
            ?: resolveFromScopeSource(scope,instanceContext)
            ?: resolveFromScopeArchetype(scope,instanceContext)
            ?: if (lookupParent) resolveFromParentScopes(scope,instanceContext) else null
            ?: resolveInExtensions(scope,instanceContext)
    }


    private fun <T> resolveFromRegistry(
        scope: Scope,
        ctx: ResolutionContext
    ): T? {
        return _koin.instanceRegistry.resolveInstance(ctx.qualifier, ctx.clazz, scope.scopeQualifier, ctx)
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

    private inline fun <T> resolveFromScopeSource(scope: Scope, ctx: ResolutionContext): T? {
        if (scope.isRoot || scope.sourceValue == null || !(ctx.clazz.isInstance(scope.sourceValue) && ctx.qualifier == null)) return null
        ctx.logger.debug("|- ? ${ctx.debugTag} look at scope source")
        return if (ctx.clazz.isInstance(scope.sourceValue)) { scope.sourceValue as? T } else null
    }

    @KoinExperimentalAPI
    private inline fun <T> resolveFromScopeArchetype(scope: Scope, ctx: ResolutionContext): T? {
        if (scope.isRoot || scope.scopeQualifier !is TypeQualifier) return null
        ctx.logger.debug("|- ? ${ctx.debugTag} look at scope archetype")
        return _koin.instanceRegistry.resolveScopeArchetypeInstance<T>(ctx.qualifier, ctx.clazz, ctx)
    }

    private fun <T> resolveFromParentScopes(scope: Scope, ctx: ResolutionContext): T? {
        if (scope.isRoot) return null
        ctx.logger.debug("|- ? ${ctx.debugTag} look in other scopes")
        return findInOtherScope(scope,ctx)
    }

    private fun <T> findInOtherScope(
        scope: Scope,
        ctx: ResolutionContext,
    ): T? {
        val parentScopes = if (scope.linkedScopes.size > 1) flatten(scope.linkedScopes) else scope.linkedScopes
        return parentScopes.firstNotNullOfOrNull {
            ctx.logger.debug("|- ? ${ctx.debugTag} look in scope '${it.id}'")
            val instanceContext = if (!it.isRoot) ctx.newContextForScope(it) else ctx
            resolveFromContextOrNull(it, instanceContext, lookupParent = false)
        }
    }

    private inline fun <T> throwNoDefinitionFound(ctx: ResolutionContext): T {
        val qualifierString = ctx.qualifier?.let { " and qualifier '$it'" } ?: ""
        throw NoDefinitionFoundException(
            "No definition found for type '${ctx.clazz.getFullName()}'$qualifierString. Check your Modules configuration and add missing type and/or qualifier!",
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

@KoinInternalApi
fun flatten(scopes: List<Scope>): Set<Scope> {
    val flatten = linkedSetOf<Scope>()
    val stack = ArrayDeque(scopes.asReversed())

    while (stack.isNotEmpty()) {
        val current = stack.removeLast()
        if (!flatten.add(current)) {
            continue
        }
        for (scope in current.linkedScopes) {
            if (scope !in flatten) {
                stack += scope
            }
        }
    }

    return flatten
}