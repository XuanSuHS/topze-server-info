package top.xuansu.topzeServerInfo

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request

var mapData: JsonObject = JsonObject()

fun webForTopZE(): String {
    val token = Config.token
    val baseurl = Config.serverURL

    //构建http请求
    val okHttpclient = OkHttpClient.Builder().build()
    val request = Request.Builder()
        .url(baseurl)
        .header("clan_auth_token", token)
        .get()
        .build()
    val response = okHttpclient.newCall(request).execute()
    val responseData = response.body!!.string()
    response.close()
    //将请求转换为JsonObject并提取其中message数组部分
    val responseDataJSON = JsonParser.parseString(responseData).asJsonObject.getAsJsonArray("message")
    var serverString = "  [5E 僵尸逃跑服务器详情]\n怎么这个时候还有人在玩ze啊"
    //遍历数组中每一项中所需的数据
    for (i in 0 until responseDataJSON.size()) {
        val server = responseDataJSON.get(i).asJsonObject
        val name = server.get("Name").toString().replace("\"", "")
        val playerCount = server.get("PlayerCount").toString().replace("\"", "").toInt()
        val maxPlayer = server.get("MaxPlayer").toString().replace("\"", "").toInt()
        //服务器少于指定人数时跳过
        if (playerCount < Config.minPlayer) {
            continue
        }
        serverString = serverString.replace("\n怎么这个时候还有人在玩ze啊","")
        val gameMap = server.get("GameMap").toString().replace("\"", "")

        serverString = serverString
            .plus("\n\n$name  ")
            .plus("$playerCount/$maxPlayer\n")
            .plus("地图：$gameMap \n")

        //如果mapData中找不到此地图则不显示相关内容
        if (mapData.has(gameMap)) {
            val mapInfo = mapData.get(gameMap).asJsonObject
            val mapCNName = mapInfo.get("chinese").toString().replace("\"", "")
            val mapDifficulty = mapInfo.get("level").toString().replace("\"", "")
            val mapTag = mapInfo.get("tag").toString().replace("\"", "")
            serverString = serverString
                .plus("译名：$mapCNName\n")
                .plus("信息：$mapDifficulty $mapTag")
        }
    }
    return serverString
}

fun getMapData() {
    val serverURL =
        "https://ghproxy.net/https://raw.githubusercontent.com/mr2b-wmk/GOCommunity-ZEConfigs/master/mapchinese.cfg"
    val okHttpClient = OkHttpClient.Builder().build()
    val request = Request.Builder()
        .url(serverURL)
        .get()
        .build()
    val response = okHttpClient.newCall(request).execute()
    val mapDataString = response.body!!.string()
    val mapDataJsonOut = mapDataString
        .replace("\"\t\t\"", "\": \"")
        .replace("\"\n\t\t\"", "\",\n\t\t\"")
        .replace("}\n\t\"", "},\n\t\"")
        .replace("\"\n\t{", "\":\n\t{")
        .replace("\"MapInfo\"", "")
    mapData = JsonParser.parseString(mapDataJsonOut).asJsonObject
}