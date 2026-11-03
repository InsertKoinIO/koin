// package: com.example.sdk
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Singleton
class SdkService

class HostService

@Module
@Configuration
class HostModule {
    @Singleton
    fun hostService() = HostService()
}

fun check() {
    assertNotNull(SdkKoinContext.koin.getOrNull<SdkService>())
    assertNull(SdkKoinContext.koin.getOrNull<HostService>(), "the label must keep out modules with no label")
}
