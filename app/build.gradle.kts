import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.dagger.hilt.android")
    id ("kotlin-kapt")

}

android {
    namespace = "com.setembreiros.artis"
    compileSdk = 34

    //load the values from .properties file
    val keystoreFile = project.rootProject.file("local.properties")
    val properties = Properties()
    properties.load(keystoreFile.inputStream())

    defaultConfig {
        applicationId = "com.setembreiros.artis"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField(type = "String",name = "CLIENT_ID_UA", value = properties.getProperty("CLIENT_ID_UA") ?: "" )
        buildConfigField(type = "String",name = "CLIENT_ID_UE", value = properties.getProperty("CLIENT_ID_UE") ?: "" )
        buildConfigField(type = "String",name = "SECRET_KEY_UA", value = properties.getProperty("SECRET_KEY_UA") ?: "" )
        buildConfigField(type = "String",name = "SECRET_KEY_UE", value = properties.getProperty("SECRET_KEY_UE") ?: "" )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {

        debug {
            buildConfigField(type = "String",name = "API_URL", value = properties.getProperty("API_URL_DEBUG") ?: "")
            buildConfigField(type = "String",name = "S3_URL", value = properties.getProperty("S3_URL_DEBUG") ?: "")

        }
        release {
            buildConfigField(type = "String",name = "API_URL", value = properties.getProperty("API_URL_RELEASE") ?: "")
            buildConfigField(type = "String",name = "S3_URL", value = properties.getProperty("S3_URL_DEBUG") ?: "")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    kapt {
        correctErrorTypes = true
    }
    buildFeatures {
        compose = true
    }
    buildFeatures {
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.security.crypto)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    // HILT
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation (libs.androidx.hilt.navigation.fragment)
    implementation (libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.runtime)
    implementation (libs.androidx.lifecycle.runtime.compose)

    //Cognito
    implementation(libs.cognitoidentityprovider)
    implementation(libs.cognitoidentity)
    implementation(libs.secretsmanager)

    //Retrofit
    implementation (libs.converter.gson)
    //retrofit
    implementation (libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation (libs.converter.moshi)
    implementation (libs.converter.gson)
    implementation (libs.converter.scalars)
    //coil
    implementation(libs.coil.compose)

}