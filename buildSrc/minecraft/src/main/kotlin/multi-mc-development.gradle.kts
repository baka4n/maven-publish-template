import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import java.util.Properties

subprojects {
    apply(plugin = "multi-mc-development")
}

var settingsProperites = Properties()

val file = rootProject.file("gradle/ext/mc-dev/${name}.properties")
file.parentFile.mkdirs()
if (file.exists()) {
    file.bufferedReader(Charsets.UTF_8).use {
        settingsProperites.load(it)
    }
} else {
    settingsProperites.put("platform", "")
    settingsProperites.put("minecraft.version", "1.21")
    settingsProperites.put("parchment.mapping", "2024.07.28")
    settingsProperites.put("neo.form.version", "1.21-20240613.152323")
    settingsProperites.put("common.injects", "")
    file.bufferedWriter(Charsets.UTF_8).use {
        settingsProperites.store(it, "Minecraft Development Settings")
    }
}
when(platform()) {
    0 -> {
        apply(plugin = "net.neoforged.moddev")
        configure<NeoForgeExtension> {
            neoFormVersion = settingsProperites.getProperty("neo.form.version")
            parchment {
                minecraftVersion = settingsProperites.getProperty("minecraft.version")
                mappingsVersion = settingsProperites.getProperty("parchment.mapping")
            }
        }
    }
    1 -> {
        apply(plugin = "fabric-loom")
    }
    3 -> {
        apply(plugin = "net.neoforged.moddev")
    }
}

fun platform(): Int {

    when (settingsProperites.getProperty("platform")) {
        "fabric" -> return 1
        "forge" -> return 2
        "neoforge" -> return 3
        "common" -> return 0
    }
    return -1
}


