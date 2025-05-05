import org.junit.Test
import org.koin.android.test.verify.AndroidVerify.androidTypes
import org.koin.android.test.verify.androidVerify
import org.koin.core.module.moduleConfiguration
import org.koin.sample.sandbox.di.allModules
import org.koin.test.verify.verify

class CheckModulesTest {

    @Test
    fun `Verify Module Configuration`() {
        allModules.verify(extraTypes = androidTypes)
    }
}