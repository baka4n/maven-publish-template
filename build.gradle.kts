import cn.hutool.json.JSONObject
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `java-library`
    id("com.github.hierynomus.license") version "0.15.0"
    id("com.vanniktech.maven.publish") version "0.29.0"
    `maven-publish`
    base
    signing
}

initGradleProperties()

base {
    archivesName = getSubProjectName(rootProject)
}

var mavenToml: JSONObject = read(file("maven.toml").copy(file(("gradle/template.toml"))))


subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "base")
    apply(plugin = "signing")
    apply(plugin = "com.vanniktech.maven.publish")
    apply(plugin = "java-library")

    base {
        archivesName = getSubProjectName(rootProject)
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

    project.group = buildProperties().getProperty("mavenGroup")
    project.version =buildProperties()
        .nullPut(getVersionKey(), getBuildProperties(), "1.0.0.0", "gradle.properties manager")
    project.description =buildProperties()
        .nullPut(getDescriptionKey(), getBuildProperties(), project.name, "gradle.properties manager")

    signing {
        useGpgCmd()
        sign(publishing.publications)
    }

    mavenPublishing {
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
        coordinates(project.group.toString(), base.archivesName.get(), project.version.toString())
        this.signAllPublications()
        pom {
            name = base.archivesName.get()
            description = project.description
            inceptionYear = getCreatedTime().year.toString()
            url = getApiGithubJson().getStr("html_url")
            licenses {
                license {
                    name = mavenToml.getStr("license")
                    url = "${getApiGithubJson().getStr("svn_url")}/blob/${gitBranch()}/LICENSE"
                    description = "${getApiGithubJson().getStr("svn_url")}/blob/${gitBranch()}/LICENSE"
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
                url = getApiGithubJson().getStr("html_url")
                connection = "scm:git:${getApiGithubJson().getStr("git_url")}"
                developerConnection = "scm:git:ssh://${getApiGithubJson().getStr("ssh_url")}"
            }
        }
    }
}