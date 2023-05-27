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
    suspend fun CommandSender.ze(server: String = "0") {
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
        val id = if (server.toIntOrNull() != null) {
            server.toIntOrNull()!!
        } else {
            sendMessage("无此服务器")
            return
        }
        val getDataResponse = getData(id)
        if (getDataResponse.first) {
            webresponse = getDataResponse.second
        } else {
            sendMessage(getDataResponse.second)
            return
        }
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
            .plus("服务器最低显示人数：" + Config.minPlayer)
        if (getGroupOrNull() != null) {
            val group = getGroupOrNull()!!.id
            messageToSender = if (group in Config.enabledGroups) {
                messageToSender.plus("\n当前群聊剩余CD：" + Data.groupCDTime[group] + "秒")
            } else {
                messageToSender.plus("\n当前群聊未启用本插件")
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
    @Description("设置服务器Token")
    suspend fun CommandSender.setToken(value: String) {
        Config.token = value
        Config.save()
        sendMessage("服务器Token设置为 $value")
        return
    }

    @SubCommand("cd")
    @Description("设置 /ze 命令使用冷却")
    suspend fun CommandSender.setCD(value: Int) {
        Config.coolDownTime = value
        Config.save()
        sendMessage("ze请求冷却设置为 $value 秒")
        return
    }

    @SubCommand("reload")
    @Description("重载插件配置文件")
    suspend fun CommandSender.reload() {
        Config.reload()
        sendMessage("Config 重载完成")
        return
    }

    @SubCommand("off")
    @Description("关闭插件")
    suspend fun CommandSender.off() {
        val group: Long
        if (getGroupOrNull() != null) {
            group = getGroupOrNull()!!.id
            Config.enabledGroups.remove(group)
            Config.save()
            sendMessage("关闭本群服务器查询功能")
            return
        }
    }

    @SubCommand("on")
    @Description("开启插件")
    suspend fun CommandSender.on() {
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

    @SubCommand("clearcd")
    @Description("清除查询冷却，子选项\"all\"，\"this\"")
    suspend fun CommandSender.clearCD(arg: String = "this") {
        if (arg == "all") {

            //清除所有群的CD
            Data.groupNoMoreCoolDown = Data.groupNoMoreCoolDown.mapValues { true } as MutableMap
            Data.groupCDTime = Data.groupCDTime.mapValues { 0 } as MutableMap
            sendMessage("已清除所有群聊的CD")
            return
        }

        val group: Long
        if (arg == "this") {
            if (getGroupOrNull() != null) {
                group = getGroupOrNull()!!.id
                Data.groupNoMoreCoolDown[group] = true
                Data.groupCDTime[group] = 0
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
    @Description("更新服务器地图本地化文件")
    suspend fun CommandSender.update() {
        val updateResult = updateMapData()
        if (!updateResult.first) {
            sendMessage(updateResult.second)
        } else {
            sendMessage("地图数据更新完毕")
        }
    }

    @SubCommand("proxy")
    @Description("代理设置")
    suspend fun CommandSender.proxy(arg: String = "") {
        when (arg) {
            "on" -> {
                val checkResult = checkProxy(Config.proxyAddress)
                if (checkResult.first) {
                    Config.useProxy = true
                    Config.save()
                    sendMessage("开启代理功能")
                } else {
                    sendMessage(
                        "网络错误，请检查代理\n"
                            .plus("信息：${checkResult.second}")
                    )
                }
            }

            "off" -> {
                Config.useProxy = false
                Config.save()
                sendMessage("关闭代理功能")
            }

            else -> {
                Config.proxyAddress = arg
                sendMessage("代理地址更新为 $arg")
                Config.save()
            }
        }
    }

    @SubCommand("minPlayer")
    @Description("设置最少显示人数")
    suspend fun CommandSender.minPlayer(arg: Int) {
        Config.minPlayer = arg
        sendMessage("最低人数设置为 $arg 人")
    }
}

class ZeDevCommand : SimpleCommand(
    owner = TopZEServerInfo,
    primaryName = "ze-dev"
) {
    @Handler
    suspend fun CommandSender.info() {
        updateMapData()
        sendMessage("地图数据更新完毕")
    }
}