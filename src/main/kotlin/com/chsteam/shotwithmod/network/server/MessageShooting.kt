package com.chsteam.shotwithmod.network.server

import com.chsteam.shotwithmod.api.ShotWithModAPI
import io.netty.buffer.ByteBuf
import org.bukkit.entity.Player

class MessageShooting {
    private val player : Player
    private val shooting : Boolean

    constructor(player: Player, buf: ByteBuf) {
        this.player = player
        this.shooting = buf.readBoolean()
    }

    fun handler() {
        ShotWithModAPI.shootingPlayer[this.player] = this.shooting
    }
}