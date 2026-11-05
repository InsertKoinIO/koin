package org.koin.sample

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Koin builds UserViewModel, UserService and UserRepository when the composable asks for the ViewModel.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class HowKoinWorksTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun theScreenGetsItsViewModel() {
        rule.onNodeWithText("Hello, Alice!").assertExists()
    }
}
