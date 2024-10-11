import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import com.fasterxml.jackson.dataformat.toml.TomlMapper
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    dependencies {
        classpath("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.17.2")
        classpath("cn.hutool:hutool-json:5.8.32")
    }
}

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
var parseObj: JSONObject
var file = file("../maven.toml")
if (file.exists()) {
    file.bufferedReader(Charsets.UTF_8).use {
        parseObj = JSONUtil.parseObj(TomlMapper().readTree(it).toPrettyString())
    }
} else {
    parseObj = JSONUtil.createObj()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JvmTarget.JVM_17.toString()
    }
}

dependencies {
    implementation(gradleApi())
    implementation("cn.hutool:hutool-json:5.8.32")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.17.2")
    implementation("cn.hutool:hutool-setting:5.8.32")
    implementation("cn.hutool:hutool-http:5.8.32")
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.29.0")
    implementation("gradle.plugin.com.hierynomus.gradle.plugins:license-gradle-plugin:0.16.1")
    if (parseObj.getBool("usingMinecraft", false)) {
        implementation(project(":minecraft"))
    }

}

