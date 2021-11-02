package org.koin.compiler.generator

val DEFAULT_MODULE_HEADER = """
        package org.koin.ksp.generated
    
        import org.koin.core.KoinApplication
        import org.koin.core.module.Module
        import org.koin.core.qualifier.*
        import org.koin.dsl.*
        
    """.trimIndent()

val DEFAULT_MODULE_FUNCTION = """
        fun KoinApplication.defaultModule() = modules(defaultModule)
        val defaultModule = module {
    """.trimIndent()

val DEFAULT_MODULE_FOOTER = """
    
        }
    """.trimIndent()

val MODULE_HEADER = """
            package org.koin.ksp.generated
            import org.koin.core.qualifier.*
            import org.koin.dsl.*
            import org.koin.core.qualifier.StringQualifier
            
        """.trimIndent()