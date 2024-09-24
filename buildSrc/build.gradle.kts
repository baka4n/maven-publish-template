import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `java-library`
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
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JvmTarget.JVM_17.toString()
    }
}