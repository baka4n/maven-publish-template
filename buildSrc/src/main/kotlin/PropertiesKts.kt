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

private fun Project.getKey(appendKey: String): String {
    return if (this == rootProject) "${name}.${appendKey}" else "${rootProject.name}.${name}.${appendKey}"
}

fun Project.getVersionKey(): String {
    return getKey("version")
}
fun Project.getDescriptionKey(): String {
    return getKey("description")
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
    val file = this.rootProject.file("gradle.properties")
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

fun Project.buildProperties(): Properties {
    return Properties()
        .nullToCreate(getBuildProperties()) {
            put("mavenGroup", "io.github.baka4n")
        }
}

fun Project.getBuildProperties(): File {
    return rootProject.file("gradle/ext/build.properties")
}