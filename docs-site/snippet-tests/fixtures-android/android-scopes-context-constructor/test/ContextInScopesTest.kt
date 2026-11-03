package org.koin.sample

import android.app.Application
import androidx.fragment.app.FragmentActivity
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = MainApplication::class)
class ContextInScopesTest {

    private fun fragmentIn(activity: FragmentActivity) = ScopedFragment().also {
        activity.supportFragmentManager.beginTransaction().add(it, "scoped").commitNow()
    }

    @Test
    fun activityScopeGivesTheActivity() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().get()
        assertSame(activity, activity.value.context)
    }

    @Test
    fun retainedScopeGivesTheApplication() {
        val activity = Robolectric.buildActivity(RetainedActivity::class.java).create().get()
        assertTrue(activity.value.context is Application)
    }

    @Test
    fun fragmentScopeGivesTheApplicationEvenInAScopedActivity() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().get()
        assertTrue(fragmentIn(activity).value.context is Application)
    }

    @Test
    fun activityScopedDefinitionResolvedFromAFragmentGetsTheActivity() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().get()
        assertSame(activity, fragmentIn(activity).fromActivityScope.context)
    }

    @Test
    fun fragmentScopeInAPlainActivityGivesTheApplication() {
        val activity = Robolectric.buildActivity(PlainActivity::class.java).create().get()
        assertTrue(fragmentIn(activity).value.context is Application)
    }
}
