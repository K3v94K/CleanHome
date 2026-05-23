plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.proyectopdm2026_gt01_grupo01_limpieza"
    compileSdk = 34 // Nota: usualmente compileSdk es 34 o 35 en versiones recientes estables

    defaultConfig {
        applicationId = "com.example.proyectopdm2026_gt01_grupo01_limpieza"
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // --- LIBRERÍAS AGREGADAS PARA LA ETAPA 2 (COMPAÑERO) ---
    // Retrofit y Gson para consumir la API de forma profesional
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // -------------------------------------------------------

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}