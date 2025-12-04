/*
 * Copyright 2020-2024 Dr (dr@der.kim) and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/deng-rui/RW-HPS/blob/master/LICENSE
 */

package net.rwhps.server.plugin.internal.headless.inject.core

import com.corrodinggames.rts.union.game.units.class_409
import com.corrodinggames.rts.union.game.units.custom.class_472
import com.corrodinggames.rts.union.game.units.custom.class_581
import com.corrodinggames.rts.union.gameFramework.class_773
import com.corrodinggames.rts.union.gameFramework.i.class_992
import net.rwhps.server.game.headless.core.AbstractGameUnitData
import net.rwhps.server.struct.list.Seq
import net.rwhps.server.struct.map.ObjectMap
import net.rwhps.server.struct.map.OrderedMap
import net.rwhps.server.util.inline.findField
import net.rwhps.server.util.log.Log

/**
 * @author Dr (dr@der.kim)
 */
internal class GameUnitData : AbstractGameUnitData {
    override var useMod: Boolean
        get() = GameEngine.netEngine.field_5975
        set(value) {
            GameEngine.netEngine.field_5975 = value
        }

    override fun reloadUnitData() {
        class_472.method_1153()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getUnitData(coreName: String): OrderedMap<String, ObjectMap<String, Int>> {
        val modsData = OrderedMap<String, ObjectMap<String, Int>>()
        val gameUnitDataList: List<class_581> = class_581.field_3286 as List<class_581>

        for (data in gameUnitDataList) {
            val group = data.method_1499() ?: coreName
            if (modsData.containsKey(group)) {
                modsData[group]!![data.field_3154] = data.field_3149
            } else {
                val cache = ObjectMap<String, Int>()
                modsData[group] = cache
                cache[data.field_3154] = data.field_3149
            }
        }

        return modsData
    }

    @Suppress("UNCHECKED_CAST")
    override fun getRwModLoadInfo(): Seq<String> {
        return Seq<String>().apply {
            for (a in (GameEngine.gameEngine.field_6354::class.java.findField("field_5783")!![GameEngine.gameEngine.field_6354]
                    as ArrayList<class_992>)) {
                a.field_5803.iterator().forEach {
                    add(it.toString())
                }
            }
        }
    }

    fun kill() {
        for (fastUnit in class_773.field_4230.a()) {
            if (fastUnit is class_409) {
                class_773.field_4230.remove(fastUnit)
                GameEngine.gameEngine.field_6349.method_3131(fastUnit)
                Log.clog(fastUnit.method_1059().method_1660())
                GameEngine.data.room.call.sendSystemMessage("删掉了一个单位: ${fastUnit.method_1059().method_1660()}")
            }
        }
        // 让 Core 完成记载
        Thread.sleep(100)
    }
}