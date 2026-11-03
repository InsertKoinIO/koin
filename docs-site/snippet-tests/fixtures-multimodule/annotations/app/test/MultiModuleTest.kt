package org.koin.sample

import com.app.feature.login.resolveLoginViewModel
import com.app.feature.profile.resolveProfileViewModel
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Starts the real Application of :app. Everything it resolves is internal to another Gradle module.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class MultiModuleTest {
    @Test
    fun theGraphResolvesAcrossGradleModules() {
        resolveLoginViewModel()
        resolveProfileViewModel()
    }
}
