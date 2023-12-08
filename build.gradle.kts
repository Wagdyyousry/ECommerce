buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
    }
}
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
}