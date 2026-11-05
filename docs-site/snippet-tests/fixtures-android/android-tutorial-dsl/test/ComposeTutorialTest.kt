package org.koin.sample

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Starts the app, then does what the tutorial says: type a name and tap the button.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ComposeTutorialTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun greetsTheUser() {
        rule.onNodeWithText("Enter name").performTextInput("Bob")
        rule.onNodeWithText("Say Hello").performClick()
        rule.onNodeWithText("[UserViewModel] Hello 'Bob' (bob@example.com)!").assertExists()
    }
}
