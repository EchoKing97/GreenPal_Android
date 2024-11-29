plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.firsttest"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.firsttest"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }

    packagingOptions {
        exclude("META-INF/*")
        exclude("javax/persistence/*")
        exclude("notice.txt")
        exclude("license.txt")
    }

    configurations.all {
        resolutionStrategy {
            // 强制使用 jakarta.persistence-api 的 2.2.3 版本
            force("jakarta.persistence:jakarta.persistence-api:2.2.3")
        }
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    implementation("androidx.navigation:navigation-ui:2.5.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Spring框架
    implementation ("org.springframework:spring-core:5.3.10")
    //Spring Data JPA依赖
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.6.0")
    implementation ("org.springframework:spring-context:5.3.10") // 使用您的Spring版本
    implementation ("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web:2.6.0") // 使用最新的2.6.x版本

    //数据库连接-添加Retrofit和Gson的依赖
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.code.gson:gson:2.8.8")

    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.12.1")
    implementation ("org.bouncycastle:bcprov-jdk15to18:1.68")
    implementation ("org.projectlombok:lombok:1.18.28")
    annotationProcessor ("org.projectlombok:lombok:1.18.28")
    implementation  ("javax.validation:validation-api:1.1.0.Final")

    // Google Play服务核心库
    implementation ("com.google.android.gms:play-services-base:17.6.0")

    // Google Play服务位置库
    implementation("com.google.android.gms:play-services-location:18.0.0")


    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
}






