plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.edgedetectionapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.edgedetectionapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // --- THIS IS THE FIX (PART 1) ---
        // This block tells Gradle to use CMake for the native build
        // and points it to the correct configuration file.
        externalNativeBuild {
            cmake {
                cppFlags += "" // You can add compiler flags here if needed
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    // --- THIS IS THE FIX (PART 2) ---
    // This links the Gradle build process to your CMakeLists.txt file.
    // Without this, Gradle has no idea a C++ file exists.
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1" // Use a version appropriate for your setup
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // If you plan to use OpenCV, you'll need to add its dependency here
    // implementation("com.quickbird.opencv:opencv:4.5.3")
}

