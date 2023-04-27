package top.xuansu.topzeServerInfo

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object Data : AutoSavePluginData("Data") {
    var groupInCoolDown: MutableSet<Long> by value(mutableSetOf())
    var groupNoMoreCoolDown: MutableMap<Long, Boolean> by value(mutableMapOf())
}