plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.isuru.hettiarachchi"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.isuru.hettiarachchi"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.mlkit:translate:17.0.3")
    implementation ("androidx.lifecycle:lifecycle-runtime:2.8.7")




    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}