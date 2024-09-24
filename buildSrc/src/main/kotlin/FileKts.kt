import cn.hutool.core.io.FileUtil
import java.io.File

fun File.copy(file: File) : File {
    apply {
        if (!exists()) {
            FileUtil.copyFile(file, this)
        }
    }
    return this
}