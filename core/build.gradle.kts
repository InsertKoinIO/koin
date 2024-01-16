plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.kotlinMultiplatform).apply(false)
}

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
//        classpath("com.android.tools.build:gradle:7.3.0")
    }
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }

    group = "io.insert-koin"
    version = "3.5.3-wasm-rc1"
}
