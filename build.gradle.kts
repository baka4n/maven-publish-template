import cn.hutool.core.map.BiMap
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import org.gradle.internal.extensions.stdlib.uncheckedCast
import java.util.*

import kotlin.collections.ArrayList

plugins {
    id("multi-maven-publish")
}

tasks.create("licenseAdd") { // add licenses dir license
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

    file("licenses.json").writeText(obj.toStringPretty(), Charsets.UTF_8)
}

