// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Top-level build file
plugins {
    id("com.android.application") version "8.1.0" apply false
    kotlin("android") version "1.8.20" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
