/*
 * Copyright 2020-2024 Dr (dr@der.kim) and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/deng-rui/RW-HPS/blob/master/LICENSE
 */

package net.rwhps.server.plugin.internal.headless.inject.core

import net.rwhps.server.game.headless.core.AbstractGameFast
import net.rwhps.server.plugin.internal.headless.inject.lib.PlayerConnectX
import com.corrodinggames.rts.union.gameFramework.j.class_1032 as Packet

/**
 * @author Dr (dr@der.kim)
 */
class GameFast: AbstractGameFast {
    override fun filteredPacket(packet: Any): Boolean {
        if (packet is Packet) {
            val playerConnect = packet.field_6123 as PlayerConnectX?
            val type = packet.field_6124

            if (type == 140 && playerConnect != null) {
                return true
            }

            if (GameEngine.netEngine.field_5851 && playerConnect != null && !playerConnect.field_6181 && type != 105 && type != 110 && type != 111 && type != 108 && type != 160) {
                return true
            }
            return false
        }
        return false
    }
}