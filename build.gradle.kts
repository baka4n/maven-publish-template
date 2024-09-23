import cn.hutool.core.date.LocalDateTimeUtil
import cn.hutool.core.io.FileUtil
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONArray
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import cn.hutool.setting.Setting
import com.fasterxml.jackson.dataformat.toml.TomlMapper


plugins {
    id("com.github.hierynomus.license") version "0.15.0"
    id("org.jreleaser") version "1.+"
    id("org.kordamp.gradle.java-project") version "0.+"
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

buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
    }
    dependencies {
        classpath("cn.hutool:hutool-json:5.8.31")

        classpath("cn.hutool:hutool-setting:5.8.31")
        classpath("cn.hutool:hutool-http:5.8.31")
    }
}

var s = Setting(file(".git/config").absolutePath)
var replace = FileUtil.readUtf8String(file(".git/HEAD")).replace("ref: refs/heads/", "")
val url = s.get("remote \"origin\"", "url")
val repo = url.replace(".git", "").replace("https://github.com/", "https://api.github.com/repos/");
val repos =
    JSONUtil.parseObj(HttpUtil.get(repo, Charsets.UTF_8))
var parse =
    LocalDateTimeUtil.parse(repos.getStr("created_at").replace("Z", "+0000"), "yyyy-MM-dd'T'HH:mm:ssZ")



var file = file("maven.toml")
if (!file.exists()) {
    FileUtil.copyFile(file("gradle/template.toml"), file)
}
var mavenToml: JSONObject = JSONUtil.createObj()
file.bufferedReader(Charsets.UTF_8).use {
    val readTree = TomlMapper().readTree(it)
    mavenToml = JSONUtil.parseObj(readTree.toPrettyString())
}



allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://central.sonatype.com/api/v1/publisher/deployments/download/")

        }
    }

    base {
        archivesName = project.name
    }

    config {
        info {
            name = project.base.archivesName.get()
            description = project.description
            inceptionYear = parse.year.toString()
            vendor = repos.getJSONObject("owner").getStr("login")

            links {
                website = repos.getStr("html_url")
                issueTracker = "${repos.getStr("html_url")}/issues"
                scm = repos.getStr("clone_url")
            }

            scm {
                url = repos.getStr("html_url")
                connection = "scm:git:${repos.getStr("clone_url")}"
                developerConnection = "scm:git:${repos.getStr("ssh_url")}"
            }

            val authors: JSONArray = mavenToml.getJSONArray("authors")
            people {
                for (any in authors) {
                    any as JSONObject
                    person {
                        id = any.getStr("id")
                        name = any.getStr("name")
                        any.getStr("email")?.let {
                            email = it
                        }
                        any.getStr("url")?.let {
                            url = it
                        }
                        any.getJSONObject("organization")?.let { org ->
                            organization {
                                name = org.getStr("name")
                                url = org.getStr("url")
                            }
                        }

                    }

                }
            }
        }
    }
}



subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.kordamp.gradle.java-project")
    apply(plugin = "org.jreleaser")
}

