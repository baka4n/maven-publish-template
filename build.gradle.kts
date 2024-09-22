import cn.hutool.core.io.FileUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import cn.hutool.setting.Setting

plugins {
    id("com.tddworks.central-portal-publisher") version "0.0.5"
    base
    signing
}

val mavenGroup: String by rootProject
val projectVersion: String by rootProject
val projName: String by rootProject
val projDescription: String by rootProject
group = mavenGroup
version = projectVersion
description = projDescription
base {
    archivesName = projName
}
buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
    }
    dependencies {
        classpath("cn.hutool:hutool-json:5.8.31")

        classpath("cn.hutool:hutool-setting:5.8.31")
    }
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()

    }
}

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
}

var mavenFile = file("center-maven.json")
var createObj: JSONObject;
if (!mavenFile.exists()) {
    createObj = JSONUtil.createObj()
    createObj.putOnce("user_name", "")
    createObj.putOnce("password", "")
    createObj.putOnce("signing.keyId", "")
    createObj.putOnce("signing.password", "")
    mavenFile.createNewFile()
    mavenFile.bufferedWriter(Charsets.UTF_8).use {
        it.write(createObj.toStringPretty())
    }
} else {
    mavenFile.bufferedReader(Charsets.UTF_8).use {
        createObj = JSONUtil.parseObj(it.readText())
    }
}

var s = Setting(file(".git/config").absolutePath)
println()

allprojects {
    setProperty("signing.keyId", createObj.getStr("signing.keyId"))
    setProperty("signing.password", createObj.getStr("signing.password"))
    setProperty("POM_NAME", projName)
    setProperty("POM_DESCRIPTION", projDescription)
    val url = s.get("remote \"origin\"", "url")
    setProperty("POM_URL", url.replace(".git", ""))
    setProperty("POM_SCM_URL", url.replace(".git", ""))
    setProperty("POM_SCM_CONNECTION", url)
    setProperty("POM_SCM_DEV_CONNECTION", url)
}

sonatypePortalPublisher {
    authentication {
        username = createObj.getStr("user_name")
        password = createObj.getStr("password")
    }
    settings {
        autoPublish = false
        aggregation = true
    }
}
