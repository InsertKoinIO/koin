import org.gradle.api.tasks.wrapper.Wrapper.DistributionType
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.gradle.ext.ActionDelegationConfig
import org.jetbrains.gradle.ext.ActionDelegationConfig.TestRunner
import org.jetbrains.gradle.ext.EncodingConfiguration
import org.jetbrains.gradle.ext.IdeaCompilerConfiguration
import org.jetbrains.gradle.ext.ProjectSettings

plugins {
    id("org.jetbrains.gradle.plugin.idea-ext")
}

tasks {
    wrapper {
        distributionType = DistributionType.ALL
    }
}

gradleRunConfiguration(tasks = listOf("clean"))
gradleRunConfiguration(cfgSubName = "wrapper, refresh dependencies", tasks = listOf("clean", "wrapper")) {
    scriptParameters = "--refresh-dependencies"
}
gradleRunConfiguration(cfgSubName = "all tests", tasks = listOf("check"))
gradleRunConfiguration(cfgSubName = "local publish", tasks = listOf("publishToMavenLocal"))

configure<IdeaModel> {
    project {
        this as ExtensionAware

        configure<ProjectSettings> {
            this as ExtensionAware

            doNotDetectFrameworks("AngularCLI", "django", "ejb", "Python", "google-appengine-python", "buildout-python")

            configure<EncodingConfiguration> {
                encoding = "UTF-8"
            }

            configure<ActionDelegationConfig> {
                delegateBuildRunToGradle = true
                testRunner = TestRunner.GRADLE
            }

            configure<IdeaCompilerConfiguration> {
                displayNotificationPopup = true
                autoShowFirstErrorInEditor = true
                parallelCompilation = true
                processHeapSize = 1024
                clearOutputDirectory = true
                rebuildModuleOnDependencyChange = true
            }
        }
    }
}

// for delete
subprojects.filter {
    it.projectDir.path.contains("/koin-projects/")
}.forEach { sp ->
    //    sp.apply<koin.DependencyManagementPlugin>()
}
