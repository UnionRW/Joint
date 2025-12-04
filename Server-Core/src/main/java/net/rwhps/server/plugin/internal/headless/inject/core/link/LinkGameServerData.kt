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
import net.rwhps.server.game.headless.core.link.AbstractLinkGameServerData
import net.rwhps.server.game.headless.core.link.AbstractLinkPlayerData
import net.rwhps.server.plugin.internal.headless.inject.core.GameEngine
import net.rwhps.server.util.WaitResultUtils
import net.rwhps.server.util.inline.findField
import net.rwhps.server.util.log.exp.ImplementedException

/**
 * Link The game comes with settings to avoid some of the distractions caused by confusion
 *
 * @author Dr (dr@der.kim)
 */
class LinkGameServerData : AbstractLinkGameServerData {
    override val teamOperationsSyncObject: Any
        get() = GameEngine.netEngine::class.java.findField("field_5931")!!.get(GameEngine.netEngine)

    override var maxUnit: Int
        set(value) {
            GameEngine.netEngine.field_5926 = value
            GameEngine.netEngine.field_5925 = value
        }
        get() = GameEngine.netEngine.field_5926

    override var sharedcontrol: Boolean
        set(value) {
            GameEngine.netEngine.field_5874.l = value
        }
        get() = GameEngine.netEngine.field_5874.l

    override var fog: Int
        set(value) {
            GameEngine.netEngine.field_5874.field_6015 = value
        }
        get() = GameEngine.netEngine.field_5874.field_6015

    override var nukes: Boolean
        set(value) {
            GameEngine.netEngine.field_5874.field_6020 = value
        }
        get() = GameEngine.netEngine.field_5874.field_6020

    override var credits: Int
        set(value) {
            GameEngine.netEngine.field_5874.c = value
        }
        get() = GameEngine.netEngine.field_5874.c

    override var aiDifficuld: Int
        set(value) {
            GameEngine.netEngine.field_5874.field_6017 = value
        }
        get() = GameEngine.netEngine.field_5874.field_6017

    override var income: Float
        set(value) {
            GameEngine.netEngine.field_5874.field_6019 = value
        }
        get() = GameEngine.netEngine.field_5874.field_6019

    override var startingunits: Int
        set(value) {
            GameEngine.netEngine.field_5874.g = value
        }
        get() = GameEngine.netEngine.field_5874.g

    override fun getPlayerData(position: Int): AbstractLinkPlayerData {
        return PrivateClassLinkPlayer(WaitResultUtils.waitResult { class_324.method_526(position) }
            ?: throw ImplementedException.PlayerImplementedException(
                "[PlayerData-New] Player is invalid"
            ))
    }

    override fun getPlayerAIData(position: Int): AbstractLinkPlayerData {
        return PrivateClassLinkAIPlayer(WaitResultUtils.waitResult { class_324.method_526(position) }
            ?: throw ImplementedException.PlayerImplementedException(
                "[PlayerData-New] AI is invalid"
            ))
    }
}