package org.koin.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.dsl.module

class InstanceRegistryTest {

    @Test
    fun `createEagerInstances includes recursively loaded modules`() {
        var instanceCount = 0
        val koinApp = startKoin { }

        koinApp.modules(
            module {
                single(createdAtStart = true) {
                    instanceCount += 1

                    koinApp.modules(
                        module {
                            single(createdAtStart = true) {
                                instanceCount += 1
                                "String"
                            }
                        }
                    )
                }

                single(createdAtStart = true) {
                    instanceCount += 1
                    123
                }
            }
        )

        koinApp.createEagerInstances()
        assertThat(instanceCount).isEqualTo(3)
    }
}
