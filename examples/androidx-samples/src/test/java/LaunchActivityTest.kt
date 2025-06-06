import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.sample.sandbox.main.MainActivity
import org.koin.sample.sandbox.mvp.MVPActivity
import org.koin.test.AutoCloseKoinTest
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
//@Config(instrumentedPackages = ["androidx.loader.content"], application = Application::class)
class MainActivityTest: AutoCloseKoinTest() {

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
//        startKoin {
//            androidContext(context)
//            modules(appModule)
//        }
    }

    @Test
    fun launch_view() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity {

        }
    }

    @Test
    fun launch_view_2() {
        val scenario = ActivityScenario.launch(MVPActivity::class.java)
        scenario.onActivity {

        }
    }
}