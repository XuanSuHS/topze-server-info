package top.xuansu.topzeServerInfo

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import top.xuansu.topzeServerInfo.TopZEServerInfo.save

//主命令
class ZeCommand : SimpleCommand(TopZEServerInfo, "ze") {
    private var webresponse = ""
    @Handler
    suspend fun CommandSender.ze() {
        val group: Long
        if (getGroupOrNull() != null) {
            group = getGroupOrNull()!!.id
            //如果本群在cd则退出
            if (group in Data.groupInCooldown) {
                return
            }
            //不在则将本群加入cd中
            GroupCooldown(group)
        }
        webresponse = webForTopZE()
        sendMessage(webresponse)
    }
}

//ze-info命令
class ZeInfoCommand : SimpleCommand(
    owner = TopZEServerInfo,
    primaryName = "ze-info") {
    @Handler
    suspend fun CommandSender.info() {
        val messageToSender = "服务器地址：" + Config.serverURL + "\n"
            .plus("当前使用的Token：" + Config.token + "\n")
            .plus("当前设置的CD：" + Config.coolDownTime + "ms")
        sendMessage(messageToSender)
    }
}

class ZeSetCommand : CompositeCommand(
    owner = TopZEServerInfo,
    primaryName = "ze-set"
) {
    @SubCommand("token")
    fun setToken(value: String) {
        Config.token = value
        Config.save()
        return
    }

    @SubCommand("url")
    fun setURL(value: String) {
        Config.serverURL = value
        Config.save()
        return
    }

    @SubCommand("cd")
    fun setCD(value: Int) {
        Config.coolDownTime = value
        Config.save()
        return
    }
}