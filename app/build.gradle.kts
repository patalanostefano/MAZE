plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.maze"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.maze"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ndk {
            abiFilters += setOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
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
        sourceCompatibility = JavaVersion.VERSION_17  // Update to Java 17
        targetCompatibility = JavaVersion.VERSION_17  // Update to Java 17
    }
    kotlinOptions {
        jvmTarget = "17"  // Update to Java 17
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            // Add these lines to handle MongoDB conflicts
            pickFirsts += "META-INF/native-image/org.mongodb/bson/native-image.properties"
            pickFirsts += "META-INF/native-image/org.mongodb/mongodb-driver-core/native-image.properties"
            pickFirsts += "META-INF/native-image/org.mongodb/mongodb-driver-sync/native-image.properties"
            // Generic MongoDB related excludes
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/*.kotlin_module"
        }
    }
}

dependencies {
    // OpenCV dependency
    implementation ("com.quickbirdstudios:opencv:4.5.3.0")
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.0")) //33.7.0 sugg by firestore
    // sugg by firestore implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-firestore-ktx")

    //json
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    // Firebase SDK for Storage
    implementation ("com.google.android.gms:play-services-base:18.2.0")
    implementation ("com.google.firebase:firebase-storage-ktx")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")  // Add this
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")  // Update version
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    //wifi
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.1")

    //Firestore for accounts
    implementation("com.google.firebase:firebase-firestore-ktx")

    //Jetpack datastore for account management
    implementation("androidx.datastore:datastore-preferences:1.0.0")


    //coil
    implementation("io.coil-kt:coil-compose:2.4.0")
    // Core dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)


    // Network Service Discovery
    implementation ("androidx.core:core-ktx:1.12.0")

    // Compose UI
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.4")

    // ViewModel and Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}



