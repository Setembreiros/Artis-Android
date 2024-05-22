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

    defaultConfig {
        applicationId = "com.setembreiros.artis"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        //load the values from .properties file
        val keystoreFile = project.rootProject.file("secret.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())

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
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        kotlinCompilerExtensionVersion = "1.5.1"
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
    implementation("androidx.security:security-crypto:1.1.0-alpha03")
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

    implementation("aws.sdk.kotlin:cognitoidentityprovider:1.0.30")
    implementation("aws.sdk.kotlin:cognitoidentity:1.0.30")
    implementation("aws.sdk.kotlin:secretsmanager:1.0.30")

}