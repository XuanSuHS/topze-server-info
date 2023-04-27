package top.xuansu.topzeServerInfo

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import top.xuansu.topzeServerInfo.TopZEServerInfo.reload
import top.xuansu.topzeServerInfo.TopZEServerInfo.save

//主命令
class ZeCommand : SimpleCommand(TopZEServerInfo, "ze") {
    private var webresponse = ""

    @Handler
    suspend fun CommandSender.ze() {
        val group: Long
        if (getGroupOrNull() != null) {
            group = getGroupOrNull()!!.id
            //如果本群在cd或者未被启用则退出
            if (Data.groupCDTime[group]!! > 0 || group !in Config.enabledGroups) {
                return
            }
            //不在则将本群加入cd中
            setGroupInCoolDown(group)
        }
        webresponse = webForTopZE()
        sendMessage(webresponse)
    }
}

//ze-info命令
class ZeInfoCommand : SimpleCommand(
    owner = TopZEServerInfo,
    primaryName = "ze-info"
) {
    @Handler
    suspend fun CommandSender.info() {
        var messageToSender = "服务器地址：" + Config.serverURL + "\n"
            .plus("当前使用的Token：" + Config.token + "\n")
            .plus("当前设置的CD：" + Config.coolDownTime + " 秒\n")
            .plus("服务器最低显示人数："+ Config.minPlayer)
        if (getGroupOrNull() != null) {
            val group = getGroupOrNull()!!.id
            if (group in Config.enabledGroups) {
                messageToSender = messageToSender.plus("\n当前群聊剩余CD：" + Data.groupCDTime[group] + "秒")
            } else {
                messageToSender = messageToSender.plus("\n当前群聊未启用本插件")
            }
        }
        sendMessage(messageToSender)
    }
}

class ZeSetCommand : CompositeCommand(
    owner = TopZEServerInfo,
    primaryName = "ze-set"
) {
    @SubCommand("token")
    suspend fun CommandSender.setToken(value: String) {
        Config.token = value
        Config.save()
        sendMessage("服务器Token设置为 $value")
        return
    }

    @SubCommand("url")
    suspend fun CommandSender.setURL(value: String) {
        Config.serverURL = value
        Config.save()
        sendMessage("服务器URL设置为 $value")
        return
    }

    @SubCommand("cd")
    suspend fun CommandSender.setCD(value: Int) {
        Config.coolDownTime = value
        Config.save()
        sendMessage("ze请求冷却设置为 $value 秒")
        return
    }

    @SubCommand("reload")
    suspend fun CommandSender.reload() {
        Config.reload()
        sendMessage("Config 重载完成")
        return
    }

    @SubCommand("disable")
    suspend fun CommandSender.disable() {
        val group: Long
        if (getGroupOrNull() != null) {
            group = getGroupOrNull()!!.id
            Config.enabledGroups.remove(group)
            Config.save()
            sendMessage("关闭本群服务器查询功能")
            return
        }
    }

    @SubCommand("enable")
    suspend fun CommandSender.enable() {
        val group: Long
        if (getGroupOrNull() != null) {
            group = getGroupOrNull()!!.id
            Config.enabledGroups.add(group)
            Data.groupCDTime[group] = 0
            Config.save()
            sendMessage("开启本群服务器查询功能")
            return
        }
    }

    @SubCommand("removeCD")
    suspend fun CommandSender.removeCD(arg: String) {
        if (arg == "all") {

            //清除所有群的CD
            Data.groupNoMoreCoolDown = Data.groupNoMoreCoolDown.mapValues { true } as MutableMap
            sendMessage("已清除所有群聊的CD")
            return
        }

        val group: Long
        if (arg == "this") {
            if (getGroupOrNull() != null) {
                group = getGroupOrNull()!!.id
                Data.groupNoMoreCoolDown[group] = true
                sendMessage("清除本群CD")
            } else {
                sendMessage("请在群聊环境内触发")
            }
            return
        }
        sendMessage("选项仅可为\"all\"或\"this\"，请重新输入")
        return
    }

    @SubCommand("updateMapData")
    suspend fun CommandSender.updateMapData() {
        getMapData()
        sendMessage("地图数据更新完毕")
    }

    @SubCommand("minPlayer")
    suspend fun CommandSender.minPlayer(arg: Int) {
        Config.minPlayer = arg
        sendMessage("最低人数设置为 $arg 人")
    }
}