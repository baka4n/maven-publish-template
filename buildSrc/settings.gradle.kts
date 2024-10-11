import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import com.fasterxml.jackson.dataformat.toml.TomlMapper

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.17.2")
        classpath("cn.hutool:hutool-json:5.8.32")
    }
}

var parseObj: JSONObject
var file = file("../maven.toml")
if (file.exists()) {
    file.bufferedReader(Charsets.UTF_8).use {
        parseObj = JSONUtil.parseObj(TomlMapper().readTree(it).toPrettyString())
    }
} else {
    parseObj = JSONUtil.createObj()
}

if (parseObj.getBool("usingMinecraft", false)) {
    include("minecraft")
}

