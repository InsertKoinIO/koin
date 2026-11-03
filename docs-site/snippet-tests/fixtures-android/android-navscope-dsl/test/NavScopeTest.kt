package org.koin.sample

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.mp.KoinPlatform
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class NavScopeTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun theScopeSurvivesRotationAndClosesOnPop() {
        rule.runOnUiThread { Probe.navController.navigate(DetailRoute) }
        rule.waitForIdle()
        val id = Probe.detailEntryId!!
        val holder = Probe.holder
        assertNotNull(KoinPlatform.getKoin().getScopeOrNull(id))

        // A configuration change keeps the entry, its scope and its instances.
        rule.activityRule.scenario.recreate()
        rule.waitForIdle()
        assertNotNull(KoinPlatform.getKoin().getScopeOrNull(id))
        assertSame(holder, Probe.holder)

        // Popping the entry closes the scope.
        rule.runOnUiThread { Probe.navController.popBackStack() }
        rule.waitForIdle()
        assertNull(KoinPlatform.getKoin().getScopeOrNull(id))
    }
}
