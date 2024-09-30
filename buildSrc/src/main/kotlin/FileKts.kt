import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.file.PathUtil

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

fun File.copy(file: File) : File {

    apply {
        if (!exists()) {
            FileUtil.copyFile(file, this)
        }
    }
    return this
}

fun Path.copy(path: Path) : Path {

    apply {


    }
    return this
}