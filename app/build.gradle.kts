import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Firma de release. Las credenciales viven en app/keystore.properties
// (NO versionado -- ver keystore.properties.example). Si el archivo no
// existe (CI, otra maquina, build de un colaborador sin la llave), el
// release cae a la firma debug para que el build no se rompa: solo los
// .aab/.apk que se suban a Play/servidor tienen que salir de una maquina
// con el keystore real.
val keystorePropsFile = rootProject.file("app/keystore.properties")
val keystoreProps = Properties().apply {
    if (keystorePropsFile.exists()) keystorePropsFile.inputStream().use { load(it) }
}
val hayFirmaRelease = keystoreProps.getProperty("storeFile")?.let { rootProject.file(it).exists() } == true

android {
    namespace = "com.kernel94.pulsoti"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kernel94.pulsoti"
        minSdk = 26
        targetSdk = 36
        versionCode = 11
        versionName = "1.10.1"
        // Cambia esto por la URL real de tu servidor (el mismo host
        // donde corre /api de encuestas_web). En emulador Android,
        // 10.0.2.2 apunta al localhost de tu PC.
        buildConfigField("String", "API_BASE_URL", "\"https://fieldserviceplus.alwaysdata.net/nps/api/\"")
    }

    signingConfigs {
        if (hayFirmaRelease) {
            create("release") {
                storeFile = rootProject.file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = if (hayFirmaRelease) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")

    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

    // Room (cache local: cuestionario/pregunta + cola offline de encuestas)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Retrofit (habla con /api de encuestas_web)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // WorkManager (sync en background cuando regresa la señal)
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // DataStore (sesion: token + datos del usuario logueado)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Coil (foto de perfil)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Timber (logging)
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Coroutines (ya incluído por otras dependencias, pero explícito)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
}
