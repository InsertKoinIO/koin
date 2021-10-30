import com.google.devtools.ksp.symbol.KSDeclaration
import org.koin.compiler.generator.KoinCodeGenerator.Companion.LOGGER
import org.koin.compiler.metadata.KoinMetaData
import java.io.OutputStream


fun OutputStream.generateFunctionDeclarationDefinition(def: KoinMetaData.Definition.FunctionDeclarationDefinition) {
    LOGGER.logging("generate $def")
    val param = def.parameters.generateParamFunction()
    val ctor = generateConstructor(def.parameters)
    val binds = generateBindings(def.bindings)
    val qualifier = def.qualifier.generateQualifier()
    val createAtStart = if (def is KoinMetaData.Definition.FunctionDeclarationDefinition.Single) {
        if (def.createdAtStart) CREATED_AT_START else ""
    } else ""
    appendText("\n\t\t\t\t${def.keyword.keyword}($qualifier$createAtStart) { ${param}moduleInstance.${def.functionName}$ctor } $binds")
}


fun OutputStream.generateClassDeclarationDefinition(def: KoinMetaData.Definition.ClassDeclarationDefinition) {
    LOGGER.logging("generate $def")
    val param = def.constructorParameters.generateParamFunction()
    val ctor = generateConstructor(def.constructorParameters)
    val binds = generateBindings(def.bindings)
    val qualifier = def.qualifier.generateQualifier()
    val createAtStart = if (def is KoinMetaData.Definition.ClassDeclarationDefinition.Single) {
        if (def.createdAtStart) CREATED_AT_START else ""
    } else ""
    appendText("\n\t\t\t\t${def.keyword.keyword}($qualifier$createAtStart) { $param${def.packageName}.${def.className}$ctor } $binds")
}

const val CREATED_AT_START = ",createdAtStart=true"

fun List<KoinMetaData.ConstructorParameter>.generateParamFunction(): String {
    return if (any { it is KoinMetaData.ConstructorParameter.ParameterInject }) "params -> " else ""
}

fun String?.generateQualifier(): String = when {
    this == "\"null\"" -> "qualifier=null"
    this == "null" -> "qualifier=null"
    !this.isNullOrBlank() -> "qualifier=StringQualifier(\"$this\")"
    else -> "qualifier=null"
}

fun generateBindings(bindings: List<KSDeclaration>): String {
    return when {
        bindings.isEmpty() -> ""
        bindings.size == 1 -> "bind(${generateBinding(bindings.first())})"
        else -> bindings.joinToString(prefix = "binds(", separator = ",", postfix = ")") { generateBinding(it) }
    }
}

fun generateScope(scope: KoinMetaData.Scope): String {
    return when(scope){
        is KoinMetaData.Scope.ClassScope -> {
            val type = scope.type
            val packageName = type.containingFile!!.packageName.asString()
            val className = type.simpleName.asString()
            "\n\t\t\t\tscope<$packageName.$className> {"
        }
        is KoinMetaData.Scope.StringScope -> "\n\t\t\t\tscope(named(\"${scope.name}\")) {"
    }
}

fun generateBinding(declaration: KSDeclaration): String {
    val packageName = declaration.containingFile!!.packageName.asString()
    val className = declaration.simpleName.asString()
    return "$packageName.$className::class"
}

fun generateConstructor(constructorParameters: List<KoinMetaData.ConstructorParameter>): String {
    LOGGER.logging("generate ctor ...")
    return constructorParameters.joinToString(prefix = "(", separator = ",", postfix = ")") { ctorParam ->
        LOGGER.logging("generate ctor: $ctorParam")
        when (ctorParam) {
            is KoinMetaData.ConstructorParameter.Dependency -> {
                val qualifier = ctorParam.value?.let { "qualifier=StringQualifier(\"${it}\")" } ?: ""
                "get($qualifier)" // value -> qualifier =
            }
            is KoinMetaData.ConstructorParameter.ParameterInject -> "params.get()"
            is KoinMetaData.ConstructorParameter.Property -> "getProperty(\"${ctorParam.value}\")"
        }
    }
}