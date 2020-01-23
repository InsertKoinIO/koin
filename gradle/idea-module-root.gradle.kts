import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.gradle.ext.*
import org.jetbrains.gradle.ext.ActionDelegationConfig.TestRunner.GRADLE

apply<IdeaExtPlugin>()

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
                testRunner = GRADLE
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

gradleRunConfiguration(tasks = listOf("clean"))
gradleRunConfiguration(cfgSubName = "assemble", tasks = listOf("clean", "assemble"))
gradleRunConfiguration(cfgSubName = "all tests", tasks = listOf("assemble", "check"))
gradleRunConfiguration(cfgSubName = "wrapper, refresh dependencies", tasks = listOf("clean", "wrapper")) {
    scriptParameters = "--refresh-dependencies"
}
gradleRunConfiguration(cfgSubName = "local publish", tasks = listOf("publishToMavenLocal"))
gradleRunConfiguration(
    cfgSubName = "resources",
    tasks = listOf("processResources", "generateAllPomFiles", "generateAllMetadataFiles", "dokka")
) {
    scriptParameters = "--rerun-tasks"
}
