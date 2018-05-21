package org.koin.core

import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.module.Module
import org.koin.dsl.path.Path
import org.koin.log.Logger
import org.koin.log.PrintLogger
import java.util.*

/**
 * Koin ModuleDefinition Builder
 * @author - Arnaud GIULIANI
 */
class Koin(val koinContext: KoinContext) {

    val propertyResolver = koinContext.propertyResolver
    val beanRegistry = koinContext.beanRegistry
    val pathRegistry = koinContext.pathRegistry

    /**
     * Inject properties to context
     */
    fun bindAdditionalProperties(props: Map<String, Any>): Koin {
        if (props.isNotEmpty()) {
            propertyResolver.addAll(props)
        }
        return this
    }

    /**
     * Inject all properties from koin properties file to context
     */
    fun bindKoinProperties(koinFile: String = "/koin.properties"): Koin {
        val content = Koin::class.java.getResource(koinFile)?.readText()
        content?.let {
            val koinProperties = Properties()
            koinProperties.load(content.byteInputStream())
            val nb = propertyResolver.import(koinProperties)
            logger.log("[properties] loaded $nb properties from '$koinFile' file")
        }
        return this
    }

    /**
     * Inject all system properties to context
     */
    fun bindEnvironmentProperties(): Koin {
        val n1 = propertyResolver.import(System.getProperties())
        logger.log("[properties] loaded $n1 properties from properties")
        val n2 = propertyResolver.import(System.getenv().toProperties())
        logger.log("[properties] loaded $n2 properties from env properties")
        return this
    }

    /**
     * load given list of module instances into current StandAlone koin context
     */
    fun build(modules: Collection<Module>): Koin {
        modules.forEach { module ->
            registerDefinitions(module())
        }

        logger.log("[modules] loaded ${beanRegistry.definitions.size} definitions")
        return this
    }

    /**
     * Register moduleDefinition definitions & subModules
     */
    private fun registerDefinitions(
        moduleDefinition: ModuleDefinition,
        parentModuleDefinition: ModuleDefinition? = null,
        path: Path = Path.root()
    ) {
        val modulePath: Path = pathRegistry.makePath(moduleDefinition.path, parentModuleDefinition?.path)
        val consolidatedPath = if (path != Path.root()) modulePath.copy(parent = path) else modulePath

        pathRegistry.savePath(consolidatedPath)

        // Add definitions
        moduleDefinition.definitions.forEach { definition ->
            beanRegistry.declare(definition, consolidatedPath)
        }

        // Check sub contexts
        moduleDefinition.subModules.forEach { subModule ->
            registerDefinitions(
                subModule,
                moduleDefinition,
                consolidatedPath
            )
        }
    }

    companion object {
        /**
         * Koin Logger
         */
        var logger: Logger = PrintLogger()
    }
}