import android.app.Activity
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkerParameters
import org.junit.Test
import org.koin.sample.androidx.di.allModules
import org.koin.test.verify.verify

class CheckModulesTest {

    @Test
    fun `Verify Configuration`() {
        allModules.verify(
            extraTypes = listOf(
                String::class, // for Injected Id
                Context::class,
                Activity::class,
                SavedStateHandle::class,
                WorkerParameters::class
            )
        )
    }
}