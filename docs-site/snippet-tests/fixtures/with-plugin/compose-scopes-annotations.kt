import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.plugin.module.dsl.koinApplication

@Module
@ComponentScan("__PACKAGE__")
class ScanModule

@KoinApplication(configurations = ["__PACKAGE__"], modules = [ScanModule::class])
class ScanApp

fun check() {
    val koin = koinApplication<ScanApp>().koin
    koin.createScope<FeatureScope>("feature", FeatureScope()).get<FeatureRepository>()
}
