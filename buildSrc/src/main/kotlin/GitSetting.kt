import cn.hutool.core.date.LocalDateTimeUtil
import cn.hutool.core.io.FileUtil
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import cn.hutool.setting.Setting
import org.gradle.api.Project
import java.time.LocalDateTime

private var gitConfig: Setting? = null
private var gitBranch: String? = null
private var apiGithubJson: JSONObject = JSONUtil.createObj()
private var createdTime: LocalDateTime? = null

fun Project.gitConfig(): Setting {
    if (gitConfig == null) {
        gitConfig = Setting(rootProject.file(".git/config").absolutePath)
    }
    return gitConfig!!
}

fun Project.gitBranch(): String {
    if (gitBranch == null) {
        gitBranch = FileUtil.readUtf8String(project.rootProject.file(".git/HEAD")).replace("ref: refs/heads/", "").trim()
    }
    return gitBranch!!
}

fun Project.getApiGithubJson(): JSONObject {
    if (apiGithubJson.isEmpty()) {
        val apiGithubUrl = gitConfig().get("remote \"origin\"", "url")
            .replace(".git", "")
            .replace("https://github.com/", "https://api.github.com/repos/")
        apiGithubJson = JSONUtil.parseObj(HttpUtil.get(apiGithubUrl, Charsets.UTF_8))
    }
    return apiGithubJson

}

fun Project.getCreatedTime(): LocalDateTime {
    if (createdTime == null) {
        createdTime = LocalDateTimeUtil.parse(getApiGithubJson().getStr("created_at").replace("Z", "+0000"), "yyyy-MM-dd'T'HH:mm:ssZ");
    }
    return createdTime!!
}