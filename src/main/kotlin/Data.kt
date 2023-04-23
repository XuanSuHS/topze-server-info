package top.xuansu.topzeServerInfo

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object Data : AutoSavePluginData("Data") {
    var groupInCooldown: MutableSet<Long> by value(mutableSetOf())
}