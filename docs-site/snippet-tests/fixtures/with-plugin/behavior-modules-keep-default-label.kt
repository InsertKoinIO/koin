import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.plugin.module.dsl.koinApplication
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Singleton
import kotlin.test.assertNotNull

class VisibleService
class ListedService

@Module
@Configuration
class VisibleModule {
    @Singleton
    fun visibleService() = VisibleService()
}

@Module
class ListedModule {
    @Singleton
    fun listedService() = ListedService()
}

@KoinApplication(modules = [ListedModule::class])
class NoLabelApp

// concepts/context-isolation: listing modules does not turn off the "default" label.
fun check() {
    val koin = koinApplication<NoLabelApp>().koin
    assertNotNull(koin.getOrNull<ListedService>())
    assertNotNull(koin.getOrNull<VisibleService>())
}
