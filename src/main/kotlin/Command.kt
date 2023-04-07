package top.xuansu.topzeServerInfo

import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.ConsoleCommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import top.xuansu.topzeServerInfo.TopZEServerInfo.reload
import top.xuansu.topzeServerInfo.TopZEServerInfo.save

//主命令
class ZeCommand : SimpleCommand(TopZEServerInfo, "ze") {

    private var webresponse = ""
    @Handler
    suspend fun main(context: CommandContext) {
        webresponse = webfortopze()
        ConsoleCommandSender.sendMessage(webresponse)
    }
}
//ze-info命令
class ZeInfoCommand : SimpleCommand(TopZEServerInfo,"ze-info") {
    @Handler
    suspend fun info(context: CommandContext) {
        val token = Config.token
        val url = Config.serverURL
        val messagetosender = "服务器地址：$url\n".plus("当前使用的Token：$token")
        ConsoleCommandSender.sendMessage(messagetosender)
    }
}

//ze-set命令
class ZeSetCommand : SimpleCommand(TopZEServerInfo,"ze-set") {
    @Handler
    suspend fun zeset(context: CommandContext, target: String, value: String) {
        if (target == "token") {
            Config.token = value
            Config.save()
            Config.reload()
            return
        }
        if (target == "url") {
            Config.serverURL = value
            Config.save()
            Config.reload()
            return
        }
        ConsoleCommandSender.sendMessage("命令格式错误\n正确格式为/ze-set <目标> <值> ，<目标>仅接受 token 与 url")
    }
}