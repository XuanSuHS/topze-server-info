package top.xuansu.topzeServerInfo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun setGroupInCoolDown(group: Long) {
    CoroutineScope(Dispatchers.Default).launch {
        var delayTime:Long = 0
        val cdTime = Config.coolDownTime * 1000

        //开始计时前初始化计时相关参数
        Data.groupInCoolDown.add(group)
        Data.groupNoMoreCoolDown[group] = false

        while (delayTime <= cdTime && !Data.groupNoMoreCoolDown[group]!!) {
            delay(1000)
            delayTime += 1000
        }

        Data.groupInCoolDown.remove(group)
        Data.groupNoMoreCoolDown[group] = true
        return@launch
    }
    return
}