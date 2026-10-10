plugins { id("com.android.application") }
android {
    namespace = "it.reteviva.mesh"
    compileSdk = 35
    defaultConfig {
        applicationId = "it.reteviva.mesh"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1-experimental"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
dependencies {
    implementation("com.google.android.gms:play-services-nearby:19.5.0")
    testImplementation("junit:junit:4.13.2")
}
