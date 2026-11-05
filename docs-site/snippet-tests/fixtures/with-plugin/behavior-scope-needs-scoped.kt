import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.plugin.module.dsl.koinApplication
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import kotlin.test.assertNull

@Module
@ComponentScan("__PACKAGE__")
class ScanModule

@KoinApplication(configurations = ["__PACKAGE__"], modules = [ScanModule::class])
class ScanApp

class Owner

@Scope(Owner::class)
class ScopeAlone

@Scoped
class ScopedAlone

@Scope(Owner::class)
@Scoped
class ScopeAndScoped

@Scope(Owner::class)
@Factory
class ScopeAndFactory

// concepts/scopes, reference/annotations, support/troubleshooting: both annotations are needed.
fun check() {
    val scope = koinApplication<ScanApp>().koin.createScope<Owner>("owner", Owner())
    scope.get<ScopeAndScoped>()
    scope.get<ScopeAndFactory>()
    // Lookups by KClass, because the plugin rejects get<ScopeAlone>() with KOIN-D002 at build time.
    assertNull(scope.getOrNull<Any>(ScopeAlone::class))
    assertNull(scope.getOrNull<Any>(ScopedAlone::class))
}
