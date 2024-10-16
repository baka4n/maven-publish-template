@file:Suppress("UNCHECKED_CAST")

import cn.hutool.core.date.LocalDateTimeUtil
import cn.hutool.core.io.FileUtil
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import cn.hutool.setting.Setting
import com.fasterxml.jackson.dataformat.toml.TomlMapper
import com.vanniktech.maven.publish.SonatypeHost
import java.time.LocalDateTime
import java.util.*

plugins {
    base
    `maven-publish`
    signing
    `java-library`
    id("com.vanniktech.maven.publish")
    id("com.github.hierynomus.license")
}

subprojects {
    apply(plugin= "base")
    apply (plugin= "maven-publish")
    apply (plugin= "signing")
    apply (plugin= "java-library")
    apply (plugin= "com.vanniktech.maven.publish")
    base {
        archivesName = getSubProjectName(rootProject)
    }
}



base {
    archivesName.set(getSubProjectName(rootProject))
}

var s = S(
    getBuildProperties(),
    gitConfig(),
    file("maven.toml").copy(file("gradle/template.toml")).read()
)
s.apiGithubJson = getApiGithubJson()
s.buildProperties.buildProperties()
s.gitBranch = gitBranch()
readmeCreate()
licenseGeneration()

if (s.mavenToml.getBool("usingMinecraft", false)) {
    apply(plugin= "multi-mc-development")
}

allprojects {
    project.group = s.buildProperties.getProperty("mavenGroup")
    project.version = s.buildProperties.nullPut(getVersionKey(), s.buildPropertiesPath, "1.0.0.0", "gradle.properties manager")
    project.description = s.buildProperties.nullPut(getDescriptionKey(), s.buildPropertiesPath, project.name, "gradle.properties manager")
    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://maven.parchmentmc.org/")
        }
        maven {
            url = uri("https://maven.neoforged.net/releases")
        }
        maven {
            url = uri("https://repo.spongepowered.org/repository/maven-public")
        }

    }

    signing {
        useGpgCmd()
        sign(publishing.publications)
    }

    mavenPublishing {
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, true)
        coordinates(project.group.toString(), base.archivesName.get(), project.version.toString())
        signAllPublications()
        pom {
            name = base.archivesName.get()
            description = project.description
            inceptionYear = getCreatedTime().year.toString()
            url = s.apiGithubJson.getStr("html_url")
            licenses {
                license {
                    name = s.mavenToml.getStr("license")
                    url = "${s.apiGithubJson.getStr("svn_url")}/blob/${s.gitBranch}/LICENSE"
                    description = "${s.apiGithubJson.getStr("svn_url")}/blob/${s.gitBranch}/LICENSE"
                }
            }
            developers {
                val authors: List<JSONObject> = ( s.mavenToml.getJSONArray("authors") as List<JSONObject>)
                for (author in authors) {
                    developer {
                        id = author.getStr("id")
                        name = author.getStr("name")
                        url = author.getStr("url")
                    }
                }
            }
            scm {
                url = s.apiGithubJson.getStr("html_url")
                connection = "scm:git:${s.apiGithubJson.getStr("git_url")}"
                developerConnection = "scm:git:ssh://${s.apiGithubJson.getStr("ssh_url")}"
            }
        }
    }
}

tasks.register("licenseAdd") { // add licenses dir license
    this.group = "mpt"
    val jsonTarget = file("licenses.json")
    var obj = if (jsonTarget.exists()) {
        JSONUtil.readJSONObject(jsonTarget, Charsets.UTF_8)
    } else {JSONUtil.createObj()}
    file("licenses").listFiles()?.forEach {
        val sb = StringBuilder()
        it.bufferedReader(Charsets.UTF_8).use {

            for (line in it.lines()) {
                val sb1 = StringBuilder()
                for (char in line.chars()) {
                    sb1.append((char + 114514).toChar())
                }
                sb.appendLine(sb1)
            }
        }
        obj.set(it.name, sb)
        it.delete()
    }

    jsonTarget.writeText(obj.toStringPretty(), Charsets.UTF_8)
}


// other code add some

// other code ending


data class S(
    val buildPropertiesPath: File,

    val gitConfig: Setting,
    val mavenToml: JSONObject,
    var buildProperties: Properties = Properties(),
    var apiGithubJson: JSONObject = JSONUtil.createObj(),
    var gitBranch: String = "",
)

fun Project.gitBranch(): String {
    return FileUtil.readUtf8String(rootProject.file(".git/HEAD")).replace("ref: refs/heads/", "").trim()
}

fun Project.getDescriptionKey(): String {
    return getKey("description")
}

fun Project.licenseGeneration() {
    val LICENSE = rootProject.file("LICENSE")
    val authors: List<JSONObject> = ( s.mavenToml.getJSONArray("authors") as List<JSONObject>)
    val sb = StringBuilder()
    val iterator = authors.iterator()
    if (iterator.hasNext()) {
        sb.append(iterator.next().getStr("id"))
    }
    while (iterator.hasNext()) {
        sb.append(", ").append(iterator.next().getStr("id"))
    }
    LICENSE.bufferedWriter(Charsets.UTF_8).use { writer ->
        val jsonTarget = rootProject.file("licenses.json")
        for (line in JSONUtil.readJSONObject(jsonTarget, Charsets.UTF_8)
            .getStr("${s.mavenToml.getStr("license")}.template", "").lines()) {
            val sb2 = StringBuilder()
            for (char in line.chars()) {
                sb2.append((char - 114514).toChar())
            }
            writer.appendLine(sb2.toString()
                .replace("<year>", (LocalDateTime.now().year + 3).toString())
                .replace("<authors>", sb.toString()))
        }
    }
}

fun getCreatedTime(): LocalDateTime {
    return LocalDateTimeUtil.parse(getApiGithubJson().getStr("created_at").replace("Z", "+0000"), "yyyy-MM-dd'T'HH:mm:ssZ")
}

fun Project.readmeCreate() {
    val readme = rootProject.file("README.MD")
    if (readme.exists().not()) {
        readme.bufferedWriter(Charsets.UTF_8).use {
            it.appendLine("# ${rootProject.name}")
            it.appendLine(s.apiGithubJson.getStr("description"))
        }
    }
}

fun Properties.buildProperties(): Properties {
    return nullToCreate(s.buildPropertiesPath) {
        put("mavenGroup", "io.github.baka4n")
    }
}

fun Project.getBuildProperties(): File {
    return rootProject.file("gradle/ext/build.properties")
}


fun Properties.nullToCreate(path: File,  action: Action<Properties>): Properties {
    if (path.exists().not()) {
        action.execute(this)
        path.bufferedWriter(Charsets.UTF_8).use {
            store(it, "gradle.properties manager")
        }
    } else {
        path.bufferedReader(Charsets.UTF_8).use {
            load(it)
        }
    }
    return this
}

fun Project.getSubProjectName(rootProject: Project): String {
    if (this == rootProject)
        return name
    else
        return  "${rootProject.name}-${name}"
}

fun File.read(): JSONObject {
    if (exists()) {
        bufferedReader(Charsets.UTF_8).use {
            val readTree = TomlMapper().readTree(it)
            return JSONUtil.parseObj(readTree.toPrettyString())
        }
    }
    return JSONUtil.createObj()
}

fun File.copy(from: File): File {
    if (!exists()) {
        from.copyTo(this, true)
    }
    return this
}

fun getApiGithubJson(): JSONObject {
    return JSONUtil.parseObj(
        HttpUtil.get(s.gitConfig.get("remote \"origin\"", "url")
        .replace(".git", "")
        .replace("https://github.com/", "https://api.github.com/repos/"), Charsets.UTF_8))
}

fun Project.gitConfig(): Setting {
    return Setting(project.rootProject.file(".git/config").absolutePath)
}

fun Properties.nullPut(key: String, file: File, value: Any, title: String): String {
    if (!containsKey(key)) {
        put(key, value)
        file.bufferedWriter(Charsets.UTF_8).use {
            store(it, title)
        }
    }
    return getProperty(key)
}

fun Project.getVersionKey(): String {
    return project.getKey("version")
}

fun Project.getKey(appendKey: String): String {
    if (this == rootProject)
        return "${name}.${appendKey}"
    else
        return "${rootProject.name}.${name}.${appendKey}"

}
