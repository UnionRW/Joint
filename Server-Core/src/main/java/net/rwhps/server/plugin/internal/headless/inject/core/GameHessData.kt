/*
 * Copyright 2020-2024 Dr (dr@der.kim) and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/deng-rui/RW-HPS/blob/master/LICENSE
 */

package net.rwhps.server.plugin.internal.headless.inject.core

import com.corrodinggames.rts.union.game.class_324
import com.corrodinggames.rts.union.game.units.class_426
import net.rwhps.server.data.global.Data
import net.rwhps.server.dependent.redirections.game.FPSSleepRedirections
import net.rwhps.server.game.event.game.ServerGameOverEvent.GameOverData
import net.rwhps.server.game.headless.core.AbstractGameHessData
import net.rwhps.server.game.manage.MapManage
import net.rwhps.server.net.core.ConnectionAgreement
import net.rwhps.server.net.core.server.AbstractNetConnectServer
import net.rwhps.server.plugin.internal.headless.inject.core.link.PrivateClassLinkPlayer
import net.rwhps.server.plugin.internal.headless.inject.lib.PlayerConnectX
import net.rwhps.server.plugin.internal.headless.inject.net.GameVersionServer
import net.rwhps.server.struct.list.Seq
import net.rwhps.server.struct.map.ObjectMap
import net.rwhps.server.util.Time
import net.rwhps.server.util.log.Log
import org.newdawn.slick.GameContainer

/**
 * @author Dr (dr@der.kim)
 */
internal class GameHessData : AbstractGameHessData {
    override val tickHess: Int get() = GameEngine.gameEngine.field_6378
    override val tickNetHess: Int get() = GameEngine.netEngine.field_5873

    override val gameDelta: Long get() = FPSSleepRedirections.deltaMillis
    override val gameFPS: Int get() = (GameEngine.appGameContainerObject as GameContainer).fps

    override fun getWin(position: Int): Boolean {
        val teamData: class_324 = class_324.method_526(position) ?: return false

        return !teamData.method_464() && !teamData.field_1407 && !teamData.field_1406 && !teamData.field_1405
    }

    private fun getWin(player: class_324?): Boolean {
        val teamData: class_324 = player ?: return false
        return !teamData.method_464() && !teamData.field_1407 && !teamData.field_1406 && !teamData.field_1405
    }

    override fun getGameOverData(): GameOverData? {
        var lastWinTeam: Int = -1
        var lastWinCount = 0

        for (position in 0 until Data.configServer.maxPlayer) {
            val player: class_324 = class_324.method_526(position) ?: continue
            if (getWin(player) && player.field_1464 != lastWinTeam) {
                lastWinTeam = player.field_1464
                lastWinCount++
            }
        }

        if (lastWinCount == 1) {
            val winPlayer = Seq<String>().apply {
                for (position in 0 until Data.configServer.maxPlayer) {
                    val player: class_324 = class_324.method_526(position) ?: continue
                    if (player.field_1464 == lastWinTeam) {
                        add(player.field_1468)
                    }
                }
            }
            val allPlayer = Seq<String>()

            val statusData = ObjectMap<String, ObjectMap<String, Int>>().apply {
                for (position in 0 until Data.configServer.maxPlayer) {
                    val player: class_324 = class_324.method_526(position) ?: continue
                    put(player.field_1468, PrivateClassLinkPlayer(player).let {
                        ObjectMap<String, Int>().apply {
                            put("unitsKilled", it.unitsKilled)
                            put("buildingsKilled", it.buildingsKilled)
                            put("experimentalsKilled", it.experimentalsKilled)
                            put("unitsLost", it.unitsLost)
                            put("buildingsLost", it.buildingsLost)
                            put("experimentalsLost", it.experimentalsLost)
                        }
                    })
                    allPlayer.add(player.field_1468)
                }
            }

            return GameOverData(
                Time.concurrentSecond() - GameEngine.data.room.startTime,
                allPlayer,
                winPlayer,
                MapManage.maps.mapName,
                statusData,
                GameEngine.data.room.replayFileName
            )
        } else {
            return null
        }
    }

    override fun getPlayerBirthPointXY() {
        for (player in GameEngine.data.room.playerManage.playerGroup) {
            class_324.method_526(player.index).let {
                var flagA = false
                var flagB = false
                var x: Float? = null
                var y: Float? = null
                var x2: Float? = null
                var y2: Float? = null

                for (amVar in class_426.method_964()) {
                    if ((amVar is class_426) && !amVar.field_1925 && amVar.field_1927 == it) {
                        if (amVar.field_1918 && !flagA) {
                            flagA = true
                            x = amVar.field_4227
                            y = amVar.field_4228
                        }
                        if (amVar.field_1919 && !flagB) {
                            flagB = true
                            x2 = amVar.field_4227
                            y2 = amVar.field_4228
                        }
                    }
                }

                if (x == null) {
                    x = x2
                    y = y2
                }
                Log.clog("Position ${player.position} , $x $y")
            }
        }
    }

    override fun existPlayer(position: Int): Boolean {
        return class_324.method_526(position) != null
    }

    override fun getHeadlessAIServer(): AbstractNetConnectServer {
        return GameVersionServer(PlayerConnectX(GameEngine.netEngine, ConnectionAgreement(GameEngine.iRwHps!!)))
    }
}
