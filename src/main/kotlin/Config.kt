package top.xuansu.topzeServerInfo

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object Config : AutoSavePluginConfig("config") {
    // 当网络连接出现故障时，重试的次数
    var token: String by value("fec9d45f7416256443b5e051c200fc1d")
    var serverURL: String by value("https://api-clan.rushbgogogo.com/api/v1/systemApp/gameServerRoomsList?mode=ze")
    var coolDownTime: Int by value(30000)
}