plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.thirdexperiment"
    compileSdk = 33
    buildFeatures{
        viewBinding=true
    }
    defaultConfig {
        applicationId = "com.example.thirdexperiment"
        minSdk = 31
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.caverock:androidsvg:1.4")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.0")
    implementation ("androidx.recyclerview:recyclerview:1.0.0")

    implementation ("com.github.crazyandcoder:citypicker:6.0.2")

//    implementation ("com.amap.api:location:6.4.0")
    implementation ("com.amap.api:map2d:latest.integration")
    implementation ("com.amap.api:search:9.7.0")
    implementation ("com.tencent.bugly:nativecrashreport:3.8.0")


}