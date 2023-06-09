plugins {
    val kotlinVersion = "1.8.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.14.0"
}

group = "top.xuansu.topze-server-info"
version = "0.1.6-B4"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    compileOnly("com.squareup.okhttp3:okhttp:4.10.0")
}

tasks.register("printName") {
    doLast {
        println(project.name)
    }
}