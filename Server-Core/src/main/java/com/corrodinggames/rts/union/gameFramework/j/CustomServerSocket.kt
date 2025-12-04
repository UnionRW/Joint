/*
 * Copyright 2020-2024 Dr (dr@der.kim) and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/deng-rui/RW-HPS/blob/master/LICENSE
 */

package com.corrodinggames.rts.union.gameFramework.j

import net.rwhps.server.data.global.NetStaticData.netService
import net.rwhps.server.game.manage.IRwHpsManage
import net.rwhps.server.net.NetService
import net.rwhps.server.net.core.IRwHps
import net.rwhps.server.plugin.internal.headless.HessMain
import net.rwhps.server.plugin.internal.headless.inject.core.GameEngine
import net.rwhps.server.util.inline.findField
import net.rwhps.server.util.log.Log
import java.io.Closeable
import com.corrodinggames.rts.union.gameFramework.class_1061 as GameEe
import com.corrodinggames.rts.union.gameFramework.j.class_1026 as ServerAcceptRunnable

/**
 * 覆写 Game-Lib 的端口监听, 来实现 BIO->NIO
 *
 * @property netEngine NetEngine
 * @property netService NetService
 * @property port Int
 * @constructor
 *
 * @author Dr (dr@der.kim)
 */
class CustomServerSocket(var1: class_1001) : ServerAcceptRunnable(var1), Closeable {
    private val netEngine: class_1001 =
        this::class.java.findField("field_6093", class_1001::class.java)!!.get(this)!! as class_1001
    private var netServiceID = NetService.coreID()
    private var port = 0

    /**
     * 启动线程, 开启端口
     */
    override fun run() {
        if (field_6081) {
            Log.clog("Does not support UDP")
            return
        }
        GameEe.method_2981()
        Thread.currentThread().name = "NewConnectionWorker-" + (if (field_6081) "udp" else "tcp") + " - " + this.field_6080

        val iRwHps = IRwHpsManage.addIRwHps(this::class.java.classLoader, IRwHps.NetType.ServerProtocol)
        GameEngine.iRwHps = iRwHps
        HessMain.serverServerCommands.handleMessage("startnetservice $netServiceID true $port", iRwHps)
    }

    /**
     * 关闭端口监听
     */
    override fun method_2844() {
        close()
    }

    /**
     * 监听端口
     *
     * @param udp 是否是 UDP
     */
    override fun method_2846(udp: Boolean) {
        startPort(udp)
    }

    /**
     * 监听端口
     *
     * @param udp 是否是 UDP
     */
    private fun startPort(udp: Boolean) {
        field_6081 = udp
        port = netEngine.field_5973
        Log.debug("[ServerSocket] starting socket.. ${if (udp) "udp" else "tcp"} port: $port")
    }

    /**
     * 关闭端口监听
     */
    override fun close() {
        Log.debug("[Close]")
        netService.find { it.id == netServiceID }!!.stop()
        GameEngine.iRwHps = null
    }
}