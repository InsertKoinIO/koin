package org.koin.spek

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.TestBody
import org.jetbrains.spek.api.lifecycle.GroupScope
import org.jetbrains.spek.api.lifecycle.LifecycleListener
import org.koin.KoinContext
import org.koin.core.parameter.Parameters
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext

class KoinSpec(val root: Spec) : KoinComponent, Spec by root

abstract class KoinSpek(koinSpec: KoinSpec.() -> Unit): Spek({
    koinSpec.invoke(KoinSpec(this))
})

abstract class AutoCloseKoinSpek(koinSpec: KoinSpec.() -> Unit): KoinSpek({
    registerListener(object: LifecycleListener {
        override fun afterExecuteGroup(group: GroupScope) {
            StandAloneContext.closeKoin()
        }
    })

    koinSpec.invoke(this)
})

fun TestBody.dryRun(defaultParameters: Parameters = { kotlin.collections.emptyMap() }) {
    (org.koin.standalone.StandAloneContext.koinContext as KoinContext).dryRun(defaultParameters)
}
