
plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlinx-serialization")
}

kotlin {

    ios {
        binaries {
            framework {
                baseName = "shared"
            }
        }
    }

    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    val ktorVersion = "2.1.0"
    val coroutinesVersion = "1.4.2-native-mt"
    val serializationVersion = "1.0.1"
    val sqlDelightVersion = "1.4.4"

    sourceSets {
        val commonMain by getting {
            dependencies{
                api("org.jetbrains.kotlinx:kotlinx-serialization-core:${serializationVersion}")

                // HTTP
                implementation("io.ktor:ktor-client-core: $ktorVersion")
                implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:${ktorVersion}")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")


                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}")
                implementation("com.squareup.sqldelight:coroutines-extensions:${sqlDelightVersion}")

                // DI
                implementation("org.kodein.di:kodein-di:7.1.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting{
            dependencies{
                implementation("com.google.android.material:material:1.8.0")
                api("io.ktor:ktor-client-okhttp:${ktorVersion}")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutinesVersion}")
                api("com.squareup.sqldelight:android-driver:${sqlDelightVersion}")
            }
        }
        val androidUnitTest by getting

        val iosMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-ios:${ktorVersion}")
                implementation("com.squareup.sqldelight:native-driver:${sqlDelightVersion}")
            }
        }
        val iosTest by getting {
            dependsOn(commonTest)
        }
    }
}

android {
    namespace = "com.elinext.holidays"
    compileSdk = 33
    defaultConfig {
        minSdk = 26
        targetSdk = 33
    }
}
dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
}
