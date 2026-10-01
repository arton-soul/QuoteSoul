plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("androidx.room")
}

val keystoreCandidateFiles = listOf(
    rootProject.file("keystore.properties"),
    File("D:/Android/Keystore/soul/keystore.properties")
)
val keystorePropertiesFile = keystoreCandidateFiles.firstOrNull { it.exists() }

val keystoreProps = mutableMapOf<String, String>()
if (keystorePropertiesFile != null && keystorePropertiesFile.exists()) {
    keystorePropertiesFile.bufferedReader().useLines { lines ->
        lines.forEach { rawLine ->
            val line = rawLine.trim()
            if (line.isNotEmpty() && !line.startsWith("#") && line.contains("=")) {
                val idx = line.indexOf('=')
                val key = line.substring(0, idx).trim()
                val value = line.substring(idx + 1).trim()
                keystoreProps[key] = value
            }
        }
    }
}

android {
    namespace = "com.soulquote.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.dearyoti.soulquote"
        minSdk = 26
        targetSdk = 36
        versionCode = 6
        versionName = "0.6.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "DEFAULT_REMOTE_MANIFEST_URL",
            "\"https://raw.githubusercontent.com/arton-soul/QuoteSoul/main/distribution/content_manifest.json\""
        )

        buildConfigField(
            "String",
            "PRIVACY_POLICY_URL",
            "\"https://dearyoti.blogspot.com/p/privacy-policy-for-soulquote.html\""
        )
    }

    signingConfigs {
        if (keystoreProps.isNotEmpty()) {
            create("release") {
                val storeFilePath = keystoreProps["storeFile"] ?: ""
                val storeF = File(storeFilePath)
                storeFile = if (storeF.isAbsolute) storeF else rootProject.file(storeFilePath)
                storePassword = keystoreProps["storePassword"]
                keyAlias = keystoreProps["keyAlias"]
                keyPassword = keystoreProps["keyPassword"]
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            if (keystoreProps.isNotEmpty()) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }

    sourceSets["androidTest"].assets.srcDir("$projectDir/schemas")
}

kotlin {
    jvmToolchain(17)
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.08.00")
    val roomVersion = "2.8.4"

    implementation(composeBom)
    androidTestImplementation(composeBom)

    // AndroidX & Lifecycle
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.navigation:navigation-compose:2.9.4")

    // Jetpack Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Room Database
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    androidTestImplementation("androidx.room:room-testing:$roomVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")

    // Media3 ExoPlayer for Guided Meditation Audio
    val media3Version = "1.5.1"
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-common:$media3Version")
    implementation("androidx.media3:media3-session:$media3Version")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20240303")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
