import org.gradle.api.Action
import org.gradle.api.Project
import java.io.File
import java.util.*

fun Properties.nullPut(key: String, file: File, value: Any, title: String) : String {
    if (containsKey(key).not()) {
        put(key, value)
        file.bufferedWriter(Charsets.UTF_8).use {
            store(it, title)
        }
    }
    return getProperty(key)
}

private fun Properties.getKey(rootProject: Project, project: Project, appendKey: String): String {
    return if (project == rootProject) "${project.name}.${appendKey}" else "${rootProject.name}.${project.name}.${appendKey}"
}

fun Properties.getVersionKey(rootProject: Project, project: Project): String {
    return getKey(rootProject, project, "version")
}
fun Properties.getDescriptionKey(rootProject: Project, project: Project): String {
    return getKey(rootProject, project, "description")
}

fun Project.getSubProjectName(rootProject: Project): String {
    return if (this == rootProject) name  else "${rootProject.name}-${name}"
}

fun Properties.nullToCreate(path: File, action: Action<Properties>): Properties {
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

fun Project.initGradleProperties() {
    var file = this.rootProject.file("gradle.properties")
    if (file.exists().not()) {
        file.bufferedWriter(Charsets.UTF_8).use {
            val properties = Properties()
            properties.put("signing.keyId", "")
            properties.put("signing.password", "")
            properties.put("signing.secretkeyRingFile", "")
            properties.put("mavenCentralUsername", "")
            properties.put("mavenCentralPassword", "")
            properties.store(it, "gradle.properties")
        }

    }

}

private val buildProperties = Properties()
private var buildPropertiesPath: File? = null
fun Project.buildProperties(): Properties {
    if (buildProperties.isEmpty) {
        return buildProperties
            .nullToCreate(getBuildProperties()) {
                put("mavenGroup", "io.github.baka4n")
            }
    }
    return buildProperties
}

fun Project.getBuildProperties(): File {
    if (buildPropertiesPath == null) {
        buildPropertiesPath = rootProject.file("gradle/ext/build.properties")
    }
    return buildPropertiesPath!!
}