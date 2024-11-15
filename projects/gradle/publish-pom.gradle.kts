apply(plugin = "maven-publish")

configure<PublishingExtension> {
    publications {
        register<MavenPublication>("java") {
            from(components["javaPlatform"])
            pom {
                name.set("Koin")
                description.set("KOIN - Kotlin simple Dependency Injection Framework")
                url.set("https://insert-koin.io/")
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    url.set("https://github.com/InsertKoinIO/koin")
                    connection.set("scm:git:https://github.com/InsertKoinIO/koin.git")
                }
                developers {
                    developer {
                        name.set("Arnaud Giuliani")
                        email.set("arnaud@kotzilla.io")
                    }
                }
            }
        }
    }
}


apply(from = file("../../gradle/signing.gradle.kts"))