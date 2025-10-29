package org.koin.androidx.scope

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import org.koin.core.qualifier.TypeQualifier

val ActivityScopeArchetype = TypeQualifier(ComponentActivity::class)
val ActivityRetainedScopeArchetype = TypeQualifier(RetainedScopeActivity::class)
val FragmentScopeArchetype = TypeQualifier(Fragment::class)