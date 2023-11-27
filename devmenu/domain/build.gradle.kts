plugins {
    id("java-library")
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("javax.inject:javax.inject:1")
    testImplementation("junit:junit:4.13.2")
    //testImplementation("org.mockito:mockito-core:5.7.0")
    //testImplementation("org.mockito.kotlin:mockito-kotlin:5.7.0")
}
/*
sourceSets {
    test {
        java.srcDir("src/test/java") // Путь к исходным файлам тестов на Kotlin
    }
}*/