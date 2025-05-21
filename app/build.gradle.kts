plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.drwich.sleepzen"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.drwich.sleepzen"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("com.github.prolificinteractive:material-calendarview:2.0.1") {
        // Prevent pulling in the old support-compat AAR
        exclude(group = "com.android.support", module = "support-compat")
    }
    implementation(libs.room.runtime)
    annotationProcessor("androidx.room:room-compiler:2.7.1")
    implementation(libs.viewpager2)
    implementation(libs.fragment.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.room.common.jvm)
    implementation(libs.room.runtime.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}