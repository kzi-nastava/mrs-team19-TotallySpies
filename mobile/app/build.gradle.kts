import java.util.Properties

fun getLocalProperty(key: String): String {
    val props = Properties()
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { props.load(it) }
    }
    return props.getProperty(key) ?: ""
}
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.ftn.mobile"
    /*compileSdk {
        version = release(36)
    }*/

    compileSdk = 36


    defaultConfig {
        applicationId = "com.ftn.mobile"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        //ip config
        val ip = getLocalProperty("ip_address")
        val baseUrl = "http://$ip:8080/"
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


        defaultConfig {
            applicationId = "com.ftn.mobile"
            minSdk = 30
            targetSdk = 36
            versionCode = 1
            versionName = "1.0"
            //ip config
            val ip = getLocalProperty("ip_address")
            val baseUrl = "http://$ip:8080/"
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
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
            buildConfig = true
        }
    }

dependencies {
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // OkHttp
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    //Glide - for image
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    implementation("com.google.android.material:material:1.9.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
