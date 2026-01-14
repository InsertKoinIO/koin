package org.koin.androidx.scope

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.koin.core.qualifier.TypeQualifier

val ActivityScopeArchetype = TypeQualifier(AppCompatActivity::class)
val ActivityRetainedScopeArchetype = TypeQualifier(RetainedScopeActivity::class)
val FragmentScopeArchetype = TypeQualifier(Fragment::class)