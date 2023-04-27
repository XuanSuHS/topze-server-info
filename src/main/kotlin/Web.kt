package top.xuansu.topzeServerInfo

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request
import top.xuansu.topzeServerInfo.TopZEServerInfo.dataFolder
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

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
        val server = responseDataJSON.get(i)
        val name = server.asJsonObject.get("Name").toString().replace("\"", "")
        val playerCount = server.asJsonObject.get("PlayerCount").toString().replace("\"", "").toInt()
        //服务器少于指定人数时跳过
        if (playerCount < Config.minPlayer) {
            continue
        }
        serverString = serverString.replace("怎么这个时候还有人在玩ze啊","")
        val gameMap = server.asJsonObject.get("GameMap").toString().replace("\"", "")

        serverString = serverString
            .plus("\n$name  ")
            .plus("$playerCount 人\n")
            .plus("$gameMap \n")

        //如果mapchinese文件中找不到此地图则不显示相关内容
        if (mapData.has(gameMap)) {
            val mapInfo = mapData.get(gameMap).asJsonObject
            val mapCNName = mapInfo.get("chinese").toString().replace("\"", "")
            val mapDifficulty = mapInfo.get("level").toString().replace("\"", "")
            val mapTag = mapInfo.get("tag").toString().replace("\"", "")
            serverString = serverString
                .plus("译名：$mapCNName\n")
                .plus("难度：$mapDifficulty\n")
                .plus("Tag：$mapTag \n")
        }
    }
    return serverString
}

fun getMapData() {
    val path = Paths.get(File(dataFolder.path, "mapData.json").path)
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
    val inputStream = mapDataJsonOut.byteInputStream()
    Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING)
    mapData = JsonParser.parseString(mapDataJsonOut).asJsonObject
}