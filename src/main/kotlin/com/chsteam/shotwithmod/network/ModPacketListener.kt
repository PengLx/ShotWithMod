package com.chsteam.shotwithmod.network

import com.chsteam.shotwithmod.network.server.MessageAim
import com.chsteam.shotwithmod.network.server.MessageReload
import com.chsteam.shotwithmod.network.server.MessageShot
import com.chsteam.shotwithmod.network.server.MessageUnload
import io.netty.buffer.Unpooled
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

object ModPacketListener : PluginMessageListener {
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        val buf = Unpooled.wrappedBuffer(message)

        when(buf.readByte().toInt()) {
            1 -> {
                MessageAim(player, buf).handler()
            }
            2 -> {
                MessageReload(player, buf).handler()
            }
            3 -> {
                MessageShot(player, buf).handler()
            }
            4 -> {
                MessageUnload.unload(player)
            }
        }
    }
}