import java.util.Properties

var settingsPropertiesPath = file("gradle/ext/settings.properties")
settingsPropertiesPath.parentFile.mkdirs()
var settingsProperties = Properties()
if (settingsPropertiesPath.exists().not()) {
    settingsProperties.put("projName", "maven-publish-template")
    settingsPropertiesPath.bufferedWriter(Charsets.UTF_8).use {
        settingsProperties.store(it, "gradle.properties manager")
    }
} else {
    settingsPropertiesPath.bufferedReader(Charsets.UTF_8).use {
        settingsProperties.load(it)
    }
}


rootProject.name = settingsProperties.getProperty("projName")

settingsProperties.getProperty("include.projects").split(",").forEach {
    if (it.isNotEmpty()) {
        include(it)
    }
}