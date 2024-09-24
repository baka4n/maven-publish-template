import cn.hutool.core.date.LocalDateTimeUtil
import cn.hutool.core.io.FileUtil
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import cn.hutool.setting.Setting
import com.fasterxml.jackson.dataformat.toml.TomlMapper
import com.vanniktech.maven.publish.SonatypeHost
import java.util.Properties

plugins {
    `java-library`
    id("com.github.hierynomus.license") version "0.15.0"
    id("com.vanniktech.maven.publish") version "0.29.0"
    base
    signing
}

initGradleProperties()

var buildPropertiesPath = project.rootProject.file("gradle/ext/build.properties")
val buildProperties = Properties()
    .nullToCreate(buildPropertiesPath) {
        put("mavenGroup", "io.github.baka4n")
    }
val gitConfig = Setting(project.rootProject.file(".git/config").absolutePath)
val gitBranch = FileUtil.readUtf8String(project.rootProject.file(".git/HEAD")).replace("ref: refs/heads/", "").trim()

if (buildPropertiesPath.exists().not()) {
    buildProperties.put("mavenGroup", "io.github.baka4n")
    buildPropertiesPath.bufferedWriter(Charsets.UTF_8).use {
        buildProperties.store(it, "gradle.properties manager")
    }
} else {
    buildPropertiesPath.bufferedReader(Charsets.UTF_8).use {
        buildProperties.load(it)
    }
}
allprojects {
    project.group = buildProperties.getProperty("mavenGroup")
    project.version =buildProperties
        .nullPut(buildProperties
            .getVersionKey(rootProject, project), buildPropertiesPath, "1.0.0.0", "gradle.properties manager")
    project.description =buildProperties
        .nullPut(buildProperties
            .getDescriptionKey(rootProject, project), buildPropertiesPath, project.name, "gradle.properties manager")
}

allprojects {
    base {
        archivesName = getSubProjectName(rootProject)
    }
}

var s = gitConfig
var branch = gitBranch

val repos =
    JSONUtil.parseObj(HttpUtil.get(s.get("remote \"origin\"", "url")
            .replace(".git", "")
            .replace("https://github.com/", "https://api.github.com/repos/"), Charsets.UTF_8))
var parse =
    LocalDateTimeUtil.parse(repos.getStr("created_at").replace("Z", "+0000"), "yyyy-MM-dd'T'HH:mm:ssZ")




var mavenToml: JSONObject = read(file("maven.toml").copy(file(("gradle/template.toml"))))


subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "base")
    apply(plugin = "com.vanniktech.maven.publish")
    apply(plugin = "java-library")

    base {
        archivesName = "${rootProject.base.archivesName.get()}-$name"
    }
}



allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://central.sonatype.com/api/v1/publisher/deployments/download/")

        }
    }

    mavenPublishing {
        publishToMavenCentral(SonatypeHost.DEFAULT, automaticRelease = true)
        coordinates(project.group.toString(), base.archivesName.get(), project.version.toString())
        pom {
            name = base.archivesName.get()
            description = project.description
            inceptionYear = parse.year.toString()
            licenses {
                license {
                    name = mavenToml.getStr("license")
                    url = "${repos.getStr("svn_url")}/blob/$branch/LICENSE"
                    description = "${repos.getStr("svn_url")}/blob/$branch/LICENSE"
                }
            }
            developers {
                mavenToml.getJSONArray("authors").forEach {
                    it as JSONObject
                    developer {
                        id = it.getStr("id")
                        name = it.getStr("name")
                        url = it.getStr("url")
                    }
                }
            }
            scm {
                url = repos.getStr("html_url")
                connection = "scm:git:${repos.getStr("git_url")}"
                developerConnection = "scm:git:ssh://${repos.getStr("ssh_url")}"
            }
        }
    }
}
