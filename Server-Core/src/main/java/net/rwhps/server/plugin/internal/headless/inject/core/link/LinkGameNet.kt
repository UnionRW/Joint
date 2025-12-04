/*
 * Copyright 2020-2024 Dr (dr@der.kim) and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/deng-rui/RW-HPS/blob/master/LICENSE
 */

package net.rwhps.server.plugin.internal.headless.inject.core.link

import com.corrodinggames.rts.union.game.class_324
import com.corrodinggames.rts.union.gameFramework.j.CustomServerSocket
import com.corrodinggames.rts.union.gameFramework.j.class_1001
import com.corrodinggames.rts.union.gameFramework.j.class_1016
import com.corrodinggames.rts.union.gameFramework.j.class_1037
import net.rwhps.server.data.global.Data
import net.rwhps.server.game.GameMaps
import net.rwhps.server.game.event.game.ServerHessStartPort
import net.rwhps.server.game.headless.core.link.AbstractLinkGameNet
import net.rwhps.server.plugin.internal.headless.inject.core.GameEngine
import net.rwhps.server.util.inline.findField
import net.rwhps.server.util.log.Log
import java.io.IOException
import com.corrodinggames.rts.union.gameFramework.j.class_1026 as ServerAcceptRunnable

/**
 * @author Dr (dr@der.kim)
 */
internal class LinkGameNet: AbstractLinkGameNet {
    private var lastUsePort = 0
    private var lastUsePasswd: String? = ""

    override fun newConnect(ip: String, name: String) {
        try {
            //val settingsEngine = GameEngine.settingsEngine
            val netEngine = GameEngine.netEngine

            //settingsEngine.lastNetworkPlayerName = name

            //val playerName = settingsEngine.lastNetworkPlayerName

            netEngine.field_5985 = name
            val kVar2 = class_1001.method_2771(ip, false)
            netEngine.a(kVar2)
            val it: Iterator<*> = netEngine.field_5888.iterator()
            while (it.hasNext()) {
                (it.next() as class_1037).field_6174 = true
            }
        } catch (e2: IOException) {
            Log.error("[GameCore] NewConnect Error", e2)
        }
    }

    override fun startHeadlessServer(port: Int, passwd: String?) {
        val netEngine = GameEngine.netEngine

        GameEngine.settingsEngine.apply {
//            setValueDynamic("networkPort", port.toString())
//            setValueDynamic("udpInMultiplayer", false.toString())
//            setValueDynamic("saveMultiplayerReplays", )
            networkPort = port
            udpInMultiplayer = false
            saveMultiplayerReplays = Data.configServer.saveRePlayFile
        }

        netEngine.field_5973 = port
        netEngine.field_5985 = Data.headlessName

        if (Data.neverEnd) {
            netEngine.field_5979 = true
        }

        GameEngine.data.room.run {
            roomID = "Port: $port"

            try {
                GameEngine.root.hostStartWithPasswordAndMods(false, passwd, true)

                // 设置新的监听
                val tcpRunnable = CustomServerSocket(GameEngine.netEngine)
                tcpRunnable.method_2846(false)

                // SocketServer
                GameEngine.netEngine::class.java.findField("field_5880", ServerAcceptRunnable::class.java)!!
                    .set(GameEngine.netEngine, tcpRunnable)
                // SocketServer Thread
                GameEngine.netEngine::class.java.findField("field_5879", Thread::class.java)!!
                    .set(GameEngine.netEngine, Thread(tcpRunnable).apply { start() })

                // 通过特性来干掉自带的 HOST
                // 新建一个 Player[]
                class_324.method_543()
                // 进行扩展
                class_324.method_488(Data.configServer.maxPlayer, true)
                // 避免同步爆炸, 给自身设置一个隐藏队伍
                GameEngine.netEngine.field_5848 = class_324.method_526(-1)

                // 不能在启动前设置, 因为每次会变
                // 设置客户端UUID, 避免Admin/ban等不能持久化
                GameEngine.settingsEngine.setValueDynamic("networkServerId", Data.core.serverConnectUuid)

                lastUsePort = port
                lastUsePasswd = passwd

                GameEngine.data.eventManage.fire(ServerHessStartPort(GameEngine.data))

                if (Data.neverEnd) {
                    val mapPlayer = "Valley Arena (10p) [by_uber]@[z;p10]"
                    val data = mapPlayer.split("@").toTypedArray()
                    GameEngine.netEngine.field_5875 = "maps/skirmish/${data[1]}${data[0]}.tmx"
                    GameEngine.netEngine.field_5874.field_6012 = class_1016.field_6029
                    GameEngine.netEngine.field_5874.field_6013 = "${data[1]}${data[0]}.tmx"
                    GameEngine.data.room.maps.mapName = data[0]
                    GameEngine.data.room.maps.mapType = GameMaps.MapType.DefaultMap
                    GameEngine.data.gameScriptMultiPlayer.multiplayerStart()
                }
            } catch (e: Exception) {
                Log.error(e)
            }
        }
    }

    override fun closeHeadlessServer() {
        GameEngine.data.room.call.killAllPlayer()

        // 恢复
        GameEngine.netEngine.field_5848 = null

        GameEngine.root.multiplayer.disconnect("Close Server")
    }

    override fun reBootServer(run: () -> Unit) {
        if (lastUsePort == 0) {
            run()
            startHeadlessServer()
        } else {
            closeHeadlessServer()
            run()
            startHeadlessServer(lastUsePort, lastUsePasswd)
        }
    }
}