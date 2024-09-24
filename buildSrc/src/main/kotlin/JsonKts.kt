import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import com.fasterxml.jackson.dataformat.toml.TomlMapper
import java.io.File

fun read(file: File): JSONObject {
    if (file.exists()) {
        file.bufferedReader(Charsets.UTF_8).use {
            val readTree = TomlMapper().readTree(it)
            return JSONUtil.parseObj(readTree.toPrettyString())
        }
    }
    return JSONUtil.createObj()
}