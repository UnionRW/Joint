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
import net.rwhps.server.game.headless.core.link.AbstractLinkPlayerData
import net.rwhps.server.plugin.internal.headless.inject.core.GameEngine

/**
 * @author Dr (dr@der.kim)
 */
internal open class PrivateClassLinkPlayer(private val playerData: class_324) : AbstractLinkPlayerData {
    private var gameStatistics = GameEngine.gameStatistics.a(playerData)

    override fun updateDate() {
        gameStatistics = GameEngine.gameStatistics.a(playerData)
    }

    // 队伍是否为观战 && 没判定过该玩家 && 不知道 && 不知道
    override val survive get() = (!playerData.method_464() && !playerData.field_1407 && !playerData.field_1406 && !playerData.field_1405)

    /** 单位击杀数 */
    override val unitsKilled: Int get() = gameStatistics.c

    /** 建筑毁灭数 */
    override val buildingsKilled get() = gameStatistics.d

    /** 单实验单位击杀数 */
    override val experimentalsKilled get() = gameStatistics.e

    /** 单位被击杀数 */
    override val unitsLost get() = gameStatistics.f

    /** 建筑被毁灭数 */
    override val buildingsLost get() = gameStatistics.g

    /** 单实验单位被击杀数 */
    override val experimentalsLost get() = gameStatistics.h
    override val ping get() = playerData.method_539()

    /** 玩家的资金 */
    override var credits
        get() = playerData.field_1461.toInt()
        set(value) {
            playerData.field_1461 = value.toDouble()
        }

    override var name
        get() = playerData.field_1468
        set(value) {
            playerData.field_1468 = value
        }

    override val connectHexID = playerData.field_1415

    override var index
        get() = playerData.field_1457
        set(value) {
            playerData.field_1457 = value
        }
    override var team
        get() = playerData.field_1464
        set(value) {
            playerData.field_1464 = value
        }

    override var startUnit
        get() = playerData.field_1399
        set(value) {
            playerData.field_1399 = value
        }

    override var color
        get() = playerData.field_1401
        set(value) {
            playerData.field_1401 = value
        }

    override var share
        get() = playerData.field_1409
        set(value) {
            playerData.field_1409 = value
        }

    override var aiDifficulty = Int.MIN_VALUE

    override fun removePlayer() {
        playerData.method_444()
    }
}