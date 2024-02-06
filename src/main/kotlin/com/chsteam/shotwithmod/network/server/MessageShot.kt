package com.chsteam.shotwithmod.network.server

import io.netty.buffer.ByteBuf
import org.bukkit.entity.Player
import com.chsteam.shotwithmod.api.ShotWithModAPI
import com.chsteam.shotwithmod.gun.GunInstance
import org.bukkit.Bukkit

class MessageShot {
    private val player : Player
    private val rotationYaw : Float
    private val rotationPitch: Float

    constructor(player: Player, buf: ByteBuf) {
        this.player = player
        this.rotationYaw = buf.readFloat()
        this.rotationPitch = buf.readFloat()
    }

    fun handler() {
        if(ShotWithModAPI.aimingPlayer[player] == true) {
            GunInstance(this.player, this.player.inventory.itemInMainHand).shotOnAim()
        } else {
            GunInstance(this.player, this.player.inventory.itemInMainHand).shot()
        }
    }
}