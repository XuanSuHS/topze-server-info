package top.xuansu.topzeServerInfo

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request
import top.xuansu.topzeServerInfo.TopZEServerInfo.dataFolder
import java.io.File
import java.net.InetSocketAddress
import java.net.Proxy
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

var mapData: JsonObject = JsonObject()
var serverName = Array(7) { "" }
var playerCount = Array(7) { 0 }
var maxPlayer = Array(7) { 0 }
var map = Array(7) { "" }
var mapChinese = Array(7) { "" }
var mapDifficulty = Array(7) { "" }
var mapTag = Array(7) { "" }
var mapInMapData = Array(7) { false }

fun webForTopZE() {
    val token = Config.token
    val baseurl = "https://api-clan.rushbgogogo.com/api/v1/systemApp/gameServerRoomsList?mode=ze"
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
    //遍历数组中每一项中所需的数据
    for (i in 0 until responseDataJSON.size()) {
        val server = responseDataJSON.get(i).asJsonObject
        val id = when (server.get("RoomId").toString().replace("\"", "")) {
            "60079" -> {
                1
            }

            "60109" -> {
                2
            }

            "60112" -> {
                3
            }

            "60053" -> {
                4
            }

            "60847" -> {
                5
            }

            "60848" -> {
                6
            }

            else -> {
                0
            }
        }

        serverName[id] = server.get("Name").toString().replace("\"", "")
        playerCount[id] = server.get("PlayerCount").toString().replace("\"", "").toInt()
        maxPlayer[id] = server.get("MaxPlayer").toString().replace("\"", "").toInt()
        map[id] = server.get("GameMap").toString().replace("\"", "")

        if (mapData.has(map[id])) {
            mapInMapData[id] = true
            val mapInfo = mapData.get(map[id]).asJsonObject
            mapChinese[id] = mapInfo.get("chinese").toString().replace("\"", "")
            mapDifficulty[id] = mapInfo.get("level").toString().replace("\"", "")
            mapTag[id] = mapInfo.get("tag").toString().replace("\"", "")
        } else {
            mapInMapData[id] = false
        }
    }
}

fun getData(id: Int = 0): String {
    when (id) {
        0 -> {
            var response = "   [5e ZE 服务器数据]\n"
            var serverWithNotEnoughPlayers = 0
            for (i in 1..6) {
                if (playerCount[i] < Config.minPlayer) {
                    serverWithNotEnoughPlayers += 1
                    continue
                }
                response = response.plus("\n" + serverName[i])
                    .plus(" " + playerCount[i].toString() + "/" + maxPlayer[i] + "\n")
                    .plus("地图：" + map[i] + "\n")

                if (mapInMapData[i]) {
                    response = response
                        .plus("译名：" + mapChinese[i] + "\n")
                        .plus("信息：" + mapDifficulty[i] + " " + mapTag[i] + "\n")
                }
            }
            if (serverWithNotEnoughPlayers >= 6) {
                response += "\n怎么这个时候还有人在玩ze啊"
            }
            return response
        }

        in 1..6 -> {
            var response = (serverName[id])
                .plus(" " + playerCount[id].toString() + "/" + maxPlayer[id] + "\n")
                .plus("地图：" + map[id] + "\n")

            if (mapInMapData[id]) {
                response = response
                    .plus("译名：" + mapChinese[id] + "\n")
                    .plus("信息：" + mapDifficulty[id] + " " + mapTag[id] + "\n")
            }
            return response
        }

        else -> {
            return "无此服务器"
        }
    }
}

fun initializeMapData() {
    if (dataFolder.resolve("mapData.json").exists()) {
        mapData = JsonParser.parseString(dataFolder.resolve("mapData.json").readText()).asJsonObject
    } else {
    updateMapData()
    }
}

fun updateMapData() {
    val proxyAdd = Config.proxyAddress.split(":")[0]
    val proxyPort = Config.proxyAddress.split(":")[1].toInt()

    val okHttpClient = if (Config.useProxy) {
        OkHttpClient.Builder()
            .proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress(proxyAdd, proxyPort)))
            .build()
    } else {
        OkHttpClient.Builder().build()
    }

    val request = Request.Builder()
        .url(Config.mapCNURL)
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
    val path = Paths.get(File(dataFolder.path, "mapData.json").path)
    val inputStream = mapDataJsonOut.byteInputStream()
    Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING)
}