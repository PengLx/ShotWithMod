package com.chsteam.shotwithmod.network.server

import io.netty.buffer.ByteBuf
import org.bukkit.entity.Player
import com.chsteam.shotwithmod.api.ShotWithModAPI

class MessageAim {

    private val aiming : Boolean
    private val player : Player

    constructor(player: Player ,buf: ByteBuf) {
        this.player = player
        this.aiming = buf.readBoolean()

    }

    fun handler() {

        ShotWithModAPI.aimingPlayer[this.player] = this.aiming
    }
}