package org.koin.androidx.compose

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.ui.core.Modifier
import androidx.ui.core.testTag
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Text
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.fillMaxSize
import androidx.ui.test.assertLabelEquals
import androidx.ui.test.createComposeRule
import androidx.ui.test.findByTag
import androidx.ui.unit.sp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class InjectOnComposableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        val testModule = module {
            single { "Koin + Compose <3" }
        }

        startKoin {
            modules(testModule)
        }
    }

    @OptIn(ExperimentalComposeInject::class)
    private fun prepareComposable() {
        composeTestRule.setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalGravity = ContentGravity.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val message: String by inject()
                Text(message, modifier = Modifier.testTag("message"), fontSize = 26.sp)
            }
        }
    }

    @Test
    fun mustDisplayInjectedText() {
        prepareComposable()

        // Finding Compose by tag
        val compose = findByTag("message")

        // Verifying that the compose has the injected text as value
        compose.assertLabelEquals("Koin + Compose <3")
    }
}