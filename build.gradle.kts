// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        // Explicitly adding the Kotlin build tools to the classpath to resolve sync issues
    }
}

// Redirect build directory to avoid OneDrive sync locks
allprojects {
    layout.buildDirectory.set(file("${System.getProperty("user.home")}/.gradle-builds/Adoption-And-Childcare/${project.name}"))
}

// Triggering sync
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.google.services) apply false
}
