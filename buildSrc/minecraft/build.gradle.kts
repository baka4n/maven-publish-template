import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `java-library`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
        url = uri("https://maven.fabricmc.net/")
    }
    maven {
        url = uri("https://maven.neoforged.net/releases")
    }
    maven {
        url = uri("https://maven.minecraftforge.net/")
    }
}

dependencies {
    implementation(gradleApi())
    implementation("net.fabricmc:fabric-loom:1.8-SNAPSHOT")
    implementation("net.neoforged:moddev-gradle:2.0.36-beta")
    implementation("net.minecraftforge.gradle:ForgeGradle:6.0.29")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JvmTarget.JVM_17.toString()
    }
}




//dependencies {
//    implementation(gradleApi())
//}

