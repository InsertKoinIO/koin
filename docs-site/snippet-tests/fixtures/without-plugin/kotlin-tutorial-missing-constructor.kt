import org.koin.core.context.startKoin
import org.koin.core.error.NoDefinitionFoundException
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

// Records what the tutorial says about the Constructor DSL: a missing definition builds,
// and fails only when the class that needs it is resolved.
interface Repository
class Service(val repository: Repository)

fun check() {
    startKoin { modules(module { singleOf(::Service) }) }
    val failure = runCatching { KoinPlatform.getKoin().get<Service>() }.exceptionOrNull()
    check(failure is org.koin.core.error.InstanceCreationException || failure is NoDefinitionFoundException) { "Got $failure" }
    println("Runtime failure: $failure, cause: ${failure?.cause}")
}
