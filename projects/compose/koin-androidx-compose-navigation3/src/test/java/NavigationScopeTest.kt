import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.fragment.app.Fragment
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.compose.navigation3.EntryProviderInstaller
import org.koin.androidx.compose.navigation3.getEntryProvider
import org.koin.androidx.scope.activityRetainedScope
import org.koin.androidx.scope.activityScope
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.androidx.scope.fragmentScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.koin.mp.KoinPlatform
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test
import kotlin.test.assertEquals

class FakeActivity : ComponentActivity(), AndroidScopeComponent {

    override val scope by activityScope()
}

class FakeFragment : Fragment(), AndroidScopeComponent {

    override val scope by fragmentScope()
}

class FakeRetainedActivity : ComponentActivity(), AndroidScopeComponent {

    override val scope by activityRetainedScope()
}

object NavMyScreen1


@Composable
fun MyScreen1(){

}

object NavMyScreen2

@Composable
fun MyScreen2(){

}

class Navigator(startDestination: Any) {
    val backStack : SnapshotStateList<Any> = mutableStateListOf(startDestination)

    fun goTo(destination: Any){
        backStack.add(destination)
    }

    fun goBack(){
        backStack.removeLastOrNull()
    }
}

@OptIn(KoinExperimentalAPI::class)
@RunWith(RobolectricTestRunner::class)
class ActivityScopeArchetypeTest {

    @Before
    fun setup() {
        startKoin { printLogger(Level.DEBUG) }
    }

    @After
    fun stop() {
        stopKoin()
    }

    @Test
    fun `prepare all EntryProviderInstaller - scoped`() {
        val controller = Robolectric.buildActivity(FakeRetainedActivity::class.java).setup()
        val activity = controller.get()

        val koin = KoinPlatform.getKoin()
        val testModule = module {
            activityRetainedScope {
                scoped { Navigator(NavMyScreen1) }
                navigation<NavMyScreen1> { MyScreen1() }
                navigation<NavMyScreen2> { MyScreen2() }
            }
        }
        koin.loadModules(listOf(testModule))

        val scope = activity.activityRetainedScope().value
        assertEquals(2, scope.getAll<EntryProviderInstaller>().size)

        activity.getEntryProvider()
    }

    @Test
    fun `prepare all EntryProviderInstaller`() {
        val koin = KoinPlatform.getKoin()
        val testModule = module {
            single { Navigator(NavMyScreen1) }
            navigation<NavMyScreen1> { MyScreen1() }
            navigation<NavMyScreen2> { MyScreen2() }
        }
        koin.loadModules(listOf(testModule))

        val activity = FakeActivity()
        val activityScope = activity.scope
        assertEquals(2, activityScope.getAll<EntryProviderInstaller>().size)
        activity.getEntryProvider()
    }
}
