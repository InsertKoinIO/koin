import org.junit.Test
import org.koin.android.test.verify.AndroidVerify.androidTypes
import org.koin.sample.sandbox.di.appModules
import org.koin.test.verify.verify

class CheckModulesTest {

    @Test
    fun `Verify Module Configuration`() {
        appModules.verify(extraTypes = androidTypes)
    }
}