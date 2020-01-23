@file:Suppress("UnstableApiUsage")

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin

apply<DetektPlugin>()

val detekt by tasks.getting(Detekt::class)

tasks.withType(Detekt::class) {
    config.setFrom(files(rootDir.resolve("gradle/detekt.yml")))
    parallel = true
    buildUponDefaultConfig = true
    ignoreFailures = false
    reports {
        txt { enabled = false }
    }
}
