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
            val initResponse = initializeMapData()
            if (!initResponse.first) {
                logger.info { "地图数据加载错误：${initResponse.second}" }
            } else {
                logger.info { "地图信息加载完成" }
            }

            val webResponse = webForTopZE()
            if (!webResponse.first) {
                logger.info {"更新服务器信息时出错：${webResponse.second}"}
            }
        }

        while (true) {
            withContext(Dispatchers.IO) {
                Thread.sleep(15000)
            }
            CoroutineScope(Dispatchers.IO).launch {
                val webResponse = webForTopZE()
                if (!webResponse.first) {
                    logger.info {"更新服务器信息时出错：${webResponse.second}"}
                }
            }
        }
    }
}