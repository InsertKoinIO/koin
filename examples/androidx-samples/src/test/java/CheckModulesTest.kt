import org.junit.Test
import org.koin.android.test.verify.verify
import org.koin.sample.androidx.di.allModules

class CheckModulesTest {

    @Test
    fun `Verify Configuration`() {
        allModules.verify()
    }
}