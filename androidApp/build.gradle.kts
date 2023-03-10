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
        kotlinCompilerExtensionVersion = "1.4.0"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(project(":shared"))
    implementation("androidx.compose.ui:ui:1.3.1")
    implementation("androidx.compose.ui:ui-tooling:1.3.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.3.1")
    implementation("androidx.compose.foundation:foundation:1.3.1")
    implementation("androidx.compose.material:material:1.3.1")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("com.kizitonwose.calendar:compose:2.1.1")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.fragment:fragment:1.5.5")
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    implementation("androidx.appcompat:appcompat:1.6.1")

}