package top.xuansu.topzeServerInfo

import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request

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
    var serverString = ""
    //遍历数组中每一项中所需的数据
    for (i in 0 until responseDataJSON.size()){
        val server = responseDataJSON.get(i)
        val name = server.asJsonObject.get("Name").toString()
        val playerCount = server.asJsonObject.get("PlayerCount").toString()
        val gameMap = server.asJsonObject.get("GameMap").toString()
        serverString = serverString.plus("\n\n$name  ").plus(playerCount+"人\n").plus(gameMap)
    }
    return serverString.replace("\"", "")
}