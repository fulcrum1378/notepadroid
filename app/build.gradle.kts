plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.squareup.sqldelight")
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven(url = "https://jitpack.io")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "ir.mahdiparastesh.notepad"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "2.5.0"
        resourceConfigurations.addAll(
            listOf(
                "en", "fr", "ko", "nl", "pl", "zh-rCN", "it", "de", "ru", "cs",
                "pt-rBR", "no", "zh-rTW", "ar", "tr", "el", "bn", "sw", "es", "ja"
            )
        )
    }

    buildFeatures { compose = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }

    /*signingConfigs {
        create("release") {
            if(System.getenv("KSTOREFILE") != null) {
                storeFile = File(System.getenv("KSTOREFILE"))
            }

            storePassword = System.getenv("KSTOREPWD")
            keyAlias = System.getenv("KEYALIAS")
            keyPassword = System.getenv("KEYPWD")
        }
    }*/

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles.add(getDefaultProguardFile("proguard-android.txt"))
            proguardFiles.add(File("proguard-rules.pro"))
            //signingConfig = signingConfigs.getByName("release")
        }
    }

    //lint { abortOnError = false }
}

dependencies {
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.richtext)

    implementation(libs.composePreferences)
    implementation(libs.linkifyText)
    implementation(libs.sqldelight)
    implementation(libs.systemuicontroller)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.appcompat)
    implementation(libs.commonsLang)
    implementation(libs.markdownView)
    implementation(libs.material)

    implementation(libs.androidx.documentfile)
    implementation(libs.androidx.localbroadcastmanager)
}
