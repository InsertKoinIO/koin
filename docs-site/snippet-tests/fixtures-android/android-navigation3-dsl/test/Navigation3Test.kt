package org.koin.sample

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.compose.navigation3.koinEntryProvider
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class Navigation3Test {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appShowsTheStartRoute() {
        rule.setContent { App() }
        rule.onNodeWithText("Home").assertExists()
    }

    // The claim of "ViewModels for an entry": with the decorator, each entry has its own ViewModel.
    @Test
    fun eachEntryGetsItsOwnViewModel() {
        val backStack = mutableStateListOf<NavKey>(DetailRoute("a"))
        rule.setContent {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                entryProvider = koinEntryProvider<NavKey>(),
            )
        }
        rule.onNodeWithText("Detail a").assertExists()

        rule.runOnIdle { backStack.add(DetailRoute("b")) }
        rule.onNodeWithText("Detail b").assertExists()
    }

    // Why the page keeps the decorator: without it, the second entry gets the ViewModel of the first.
    @Test
    fun withoutTheDecoratorEntriesShareAViewModel() {
        val backStack = mutableStateListOf<NavKey>(DetailRoute("a"))
        rule.setContent {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = koinEntryProvider<NavKey>(),
            )
        }
        rule.onNodeWithText("Detail a").assertExists()

        rule.runOnIdle { backStack.add(DetailRoute("b")) }
        rule.onNodeWithText("Detail a").assertExists()
        rule.onNodeWithText("Detail b").assertDoesNotExist()
    }
}
