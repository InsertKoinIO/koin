// Single source of truth for every version shown on the site.
// Verified against projects/gradle.properties, projects/gradle/libs.versions.toml,
// the koin-compiler-plugin repository and Maven Central on 2026-11-02.
export const versions = {
  koin: '4.2.2',
  compilerPlugin: '1.2.1',
  kotlinMin: '2.3.20',
  // Oldest Kotlin that compiles against koin-core without the Compiler Plugin.
  // Tested on the JVM on 2026-11-02: 2.2.21 works, 2.1.21 and 2.0.21 crash the compiler.
  kotlinRuntimeMin: '2.2',
  androidMinSdk: '23',
  // The version koin-ktor is built with, and the Logback version of the repository samples.
  ktor: '3.4.0',
  logback: '1.5.16',
  // Compose Multiplatform libraries used by the KMP walkthrough, as in the koin-getting-started samples.
  // The Compose Multiplatform version Koin is built with, which it brings at runtime.
  composeMultiplatform: '1.10.2',
  navigationCompose: '2.9.1',
  lifecycleCompose: '2.9.6',
  // Android libraries of the Navigation 3 page. navigation3 is the version koin-compose-navigation3 is built with.
  navigation3: '1.0.1',
  lifecycleNavigation3: '2.10.0',
} as const;
