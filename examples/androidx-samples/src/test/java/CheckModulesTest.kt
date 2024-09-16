import org.junit.Test
import org.koin.android.test.verify.androidVerify
import org.koin.sample.sandbox.di.allModules

class CheckModulesTest {

    @Test
    fun `Verify Configuration`() {
        allModules.androidVerify()
    }
}