import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JvmTarget.JVM_17.toString()
    }
}




//dependencies {
//    implementation(gradleApi())
//}

