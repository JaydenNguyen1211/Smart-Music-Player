plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidx.navigation.safeargs)
    alias(libs.plugins.google.devtools.ksp)
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("org.jetbrains.kotlin.kapt")
}

android {
    compileSdk = 36
    namespace = "mazentas.playme.music"

    defaultConfig {
        applicationId = "mazentas.playme.music"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/LICENSE",
                "META-INF/NOTICE",
                "META-INF/java.properties"
            )
        }
    }

    lint {
        abortOnError = true
        warning.addAll(
            listOf("ImpliedQuantity", "Instantiatable", "MissingQuantity", "MissingTranslation", "StringFormatInvalid")
        )
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }

    flavorDimensions += "distribution"
    productFlavors {
        create("fdroid") {
            dimension = "distribution"
        }
        create("full") {
            dimension = "distribution"
        }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    configurations.configureEach {
        resolutionStrategy.force("com.google.code.findbugs:jsr305:1.3.9")
    }
}

dependencies {
    implementation(project(":appthemehelper"))
    implementation(libs.gridLayout)

    // AndroidX core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.constraintLayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.palette.ktx)
    implementation(libs.androidx.mediarouter)
    implementation(libs.androidx.core.splashscreen)

    // Navigation
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.common.java8)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Material
    implementation(libs.android.material)

    // Retrofit + OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp3.logging.interceptor)

    // Material Dialogs
    implementation(libs.afollestad.material.dialogs.core)
    implementation(libs.afollestad.material.dialogs.input)
    implementation(libs.afollestad.material.dialogs.color)
    implementation(libs.afollestad.material.cab)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    // Glide
    implementation(libs.glide)
    kapt(libs.glide.compiler)
    implementation(libs.glide.okhttp3.integration)

    // ExoPlayer
    implementation(libs.androidx.exoplayer)

    // Google Play
    implementation(libs.google.play.services.cast.framework)
    implementation(libs.google.feature.delivery)
    implementation(libs.google.play.review)
    implementation(libs.google.play.billing)
    implementation(libs.nanohttpd)

    // UI extras
    implementation(libs.advrecyclerview)
    implementation(libs.fadingedgelayout)
    implementation(libs.keyboardvisibilityevent)
    implementation(libs.jetradarmobile.android.snowfall)
    implementation(libs.chrisbanes.insetter)
    implementation(libs.slidableactivity)
    implementation(libs.material.intro)
    implementation(libs.fastscroll.library)
    implementation(libs.customactivityoncrash)
    implementation(libs.tankery.circularSeekBar)

    // Misc
    implementation(libs.org.eclipse.egit.github.core)
    implementation(libs.jaudiotagger)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
