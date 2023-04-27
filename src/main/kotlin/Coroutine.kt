package top.xuansu.topzeServerInfo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun setGroupInCoolDown(group: Long) {
    CoroutineScope(Dispatchers.Default).launch {

        //开始计时前初始化计时相关参数
        Data.groupCDTime[group] = Config.coolDownTime
        Data.groupNoMoreCoolDown[group] = false

        while (Data.groupCDTime[group]!! > 0) {
            if (Data.groupNoMoreCoolDown[group] == true) {
                continue
            }
            delay(1000)
            Data.groupCDTime[group] = Data.groupCDTime[group]!! - 1
        }
        return@launch
    }
    return
}