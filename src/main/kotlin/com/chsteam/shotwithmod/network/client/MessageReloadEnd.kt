package com.chsteam.shotwithmod.network.client

import com.chsteam.shotwithmod.ShotWithMod
import com.chsteam.shotwithmod.network.server.MessageReload
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.bukkit.entity.Player

object MessageReloadEnd {
    fun send(player: Player) {
        if(MessageReload.reloadMap.containsKey(player)) {
            MessageReload.reloadMap[player]?.cancel()
            MessageReload.reloadMap.remove(player)
        }
        val buf: ByteBuf = Unpooled.buffer()

        buf.writeByte(16)

        player.sendPluginMessage(ShotWithMod.plugin, "${ShotWithMod.MOD_ID}:${ShotWithMod.CHANNEL}", buf.array())
    }
}