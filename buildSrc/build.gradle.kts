import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `java-library`
    groovy
    `groovy-gradle-plugin`
}

repositories {
    maven {
        url = uri("https://maven.aliyun.com/repository/public/")
    }
    gradlePluginPortal()
    mavenCentral()
    maven {
        name = "MinecraftForge"
        url = uri("https://maven.minecraftforge.net/")
    }
    maven {
        name = "Sponge Maven"
        url = uri("https://repo.spongepowered.org/repository/maven-public/")
    }
    maven {
        name = "ParchmentMC"
        url = uri("https://maven.parchmentmc.org")
    }
}


dependencies {
    implementation(gradleApi())
    implementation("cn.hutool:hutool-json:5.8.31")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.17.2")
    implementation("cn.hutool:hutool-setting:5.8.31")
    implementation("cn.hutool:hutool-http:5.8.31")
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.29.0")
    implementation("gradle.plugin.com.hierynomus.gradle.plugins:license-gradle-plugin:0.16.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JvmTarget.JVM_17.toString()
    }
}