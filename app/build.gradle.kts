plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.compose)
    // Se aplica el plugin de Compose; la versión se resuelve en settings.gradle.kts
    id("org.jetbrains.kotlin.kapt")

}

android {
    namespace = "com.cuatroraices.appnets"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cuatroraices.appnets"
        minSdk = 23
        targetSdk = 33
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

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        compose = true  // Habilitamos Jetpack Compose
    }


}

dependencies {
    implementation(project(":curveNavX"))
    implementation(libs.androidx.core.ktx)
    implementation("com.google.firebase:firebase-analytics:22.2.0")
    implementation("com.google.firebase:firebase-auth-ktx:23.2.0")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.airbnb.android:lottie:6.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    // Única versión para el compilador de Compose (la misma que en composeOptions)
    implementation("androidx.compose.compiler:compiler:1.5.15")
    implementation("com.github.bumptech.glide:glide:4.15.1")

    kapt("com.github.bumptech.glide:compiler:4.15.1")

    implementation(libs.play.services.auth)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.ui.android)
    implementation(libs.cronet.embedded)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Dependencias de Jetpack Compose:
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("br.com.devsrsouza.compose.icons.android:feather:1.0.0")
    implementation("androidx.compose.ui:ui:1.5.3")
    implementation("androidx.compose.material:material:1.5.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
}
