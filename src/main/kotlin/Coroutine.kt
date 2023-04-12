package top.xuansu.topzeServerInfo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun groupCooldown(group: Long) {
    CoroutineScope(Dispatchers.Default).launch {
        Data.groupInCooldown.add(group)
        delay(Config.coolDownTime.toLong())
        Data.groupInCooldown.remove(group)
        return@launch
    }
    return
}