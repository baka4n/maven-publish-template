import cn.hutool.json.JSONObject
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("multi-maven-publish-template")
}

var mavenToml: JSONObject = read(file("maven.toml").copy(file(("gradle/template.toml"))))

allprojects {

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