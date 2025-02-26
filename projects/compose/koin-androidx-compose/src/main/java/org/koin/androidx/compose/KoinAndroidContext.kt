/*
 * Copyright 2017-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.koin.androidx.compose

import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.koin.android.ext.android.getKoin
import org.koin.compose.KoinContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.KoinComponent
import org.koin.mp.KoinPlatformTools


/**
 * PR Proposal https://github.com/InsertKoinIO/koin/pull/1586
 * from @jjkester
 */

/**
 * Run and bind Compose with existing Koin context, from current Android application context
 *
 * This extend the existing KoinApplication, by looking at Koin context in current Android context
 *
 * @see KoinPlatformTools.defaultContext()
 * @param content - following compose function
 *
 * @author Arnaud Giuliani
 * @author jjkester
 */
//TODO Deprecate in 4.1? - as we have default context
@Composable
fun KoinAndroidContext(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val koinApplication = remember(context) {
        context.findContextForKoin().getKoin()
    }
    KoinContext(koinApplication, content)
}

/**
 * Find the [KoinComponent] in the Context tree
 */
private fun Context.findContextForKoin(): ComponentCallbacks {
    var context = this
    while (context is ContextWrapper) {
        if (context is KoinComponent && context is ComponentCallbacks) return context
        context = context.baseContext
    }
    return applicationContext as Application
}