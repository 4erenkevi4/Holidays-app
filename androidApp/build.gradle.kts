plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.elinext.holidays.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.elinext.holidays.android"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(project(":shared"))
    implementation("androidx.compose.ui:ui:1.4.3")
    implementation("androidx.compose.ui:ui-tooling:1.4.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.3")
    implementation("androidx.compose.foundation:foundation:1.4.3")
    implementation("androidx.compose.material:material:1.4.3")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("com.kizitonwose.calendar:compose:2.1.1")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.fragment:fragment:1.6.1")
    implementation("androidx.navigation:navigation-fragment:2.6.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.15.0")



}