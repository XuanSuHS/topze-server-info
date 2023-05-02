package top.xuansu.topzeServerInfo

import kotlinx.coroutines.*
import net.mamoe.mirai.utils.info
import top.xuansu.topzeServerInfo.TopZEServerInfo.logger


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

fun backGround() {
    CoroutineScope(Dispatchers.IO).launch {
        CoroutineScope(Dispatchers.IO).launch {
            initializeMapData()
            webForTopZE()
            logger.info { "5e地图数据已加载" }
        }
        webForTopZE()
        logger.info { "5e数据已初始化" }
        while (true) {
            withContext(Dispatchers.IO) {
                Thread.sleep(15000)
            }
            CoroutineScope(Dispatchers.IO).launch {
                webForTopZE()
            }
        }
    }
}