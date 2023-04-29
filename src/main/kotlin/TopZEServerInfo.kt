package top.xuansu.topzeServerInfo

import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.jvm.reloadPluginConfig
import net.mamoe.mirai.console.plugin.jvm.reloadPluginData
import net.mamoe.mirai.utils.info

object TopZEServerInfo : KotlinPlugin(
    JvmPluginDescription(
        id = "top.xuansu.topze-server-info",
        name = "TopZE Server Info",
        version = "0.1.4",
    ) {
        author("XuanSu")
    }
) {
    override fun onEnable() {
        //初始化配置文件
        reloadPluginConfig(Config)
        reloadPluginData(Data)

        //注册指令
        ZeCommand().register()
        ZeInfoCommand().register()
        ZeSetCommand().register()

        getMapData()

        Data.groupNoMoreCoolDown = Data.groupNoMoreCoolDown.mapValues { true } as MutableMap
        Data.groupCDTime = Data.groupCDTime.mapValues { 0 } as MutableMap

        logger.info { "Plugin loaded" }
    }

    override fun onDisable() {
        super.onDisable()

        //取消注册指令
        CommandManager.INSTANCE.unregisterAllCommands(TopZEServerInfo)
    }
}