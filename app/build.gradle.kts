plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.musicrecord"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.musicrecord"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2" // Updated to match Kotlin 1.9.22
    }
}

dependencies {

    implementation("androidx.compose.ui:ui:1.7.0")
    implementation("androidx.compose.material:material:1.7.0")
//    implementation(libs.androidx.ui.tooling.preview)
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.material3:material3-android:1.3.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.7.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.5")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.0")
    //implementation("com.chibde:audiovisualizer:2.2.0")
    implementation("io.github.gautamchibde:audiovisualizer:2.2.5")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
