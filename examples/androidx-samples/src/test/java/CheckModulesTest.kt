import org.junit.Test
import org.koin.android.test.verify.AndroidVerify.androidTypes
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.sample.sandbox.di.mainModules
import org.koin.test.verify.verify

class CheckModulesTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `Verify Module Configuration`() {
        mainModules.value.verify(extraTypes = androidTypes)
    }
}
