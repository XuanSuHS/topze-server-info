package top.xuansu.topzeServerInfo

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object Data : AutoSavePluginData("Data") {

    var groupCDTime: MutableMap<Long, Int> by value(mutableMapOf())
    var groupNoMoreCoolDown: MutableMap<Long, Boolean> by value(mutableMapOf())
}