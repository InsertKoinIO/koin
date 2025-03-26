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
package org.koin.androidx.fragment.android

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import org.koin.android.ext.android.get
import org.koin.core.scope.Scope

/**
 * Set supportFragmentManager.fragmentFactory to KoinFragmentFactory
 */
fun FragmentActivity.setupKoinFragmentFactory(scope: Scope? = null) {
    if (scope == null) {
        supportFragmentManager.fragmentFactory = get()
    } else {
        supportFragmentManager.fragmentFactory = KoinFragmentFactory(scope)
    }
}

/**
 * Replace container
 *
 * @param containerViewId
 * @param args
 * @param tag
 */
inline fun <reified F : Fragment> FragmentTransaction.replace(
    @IdRes containerViewId: Int,
    args: Bundle? = null,
    tag: String? = null
): FragmentTransaction {
    return replace(containerViewId, F::class.java, args, tag)
}