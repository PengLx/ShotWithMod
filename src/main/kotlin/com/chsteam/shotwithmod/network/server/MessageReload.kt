package com.chsteam.shotwithmod.network.server

import com.chsteam.shotwithmod.ShotWithMod
import com.chsteam.shotwithmod.api.ShotWithModAPI
import com.chsteam.shotwithmod.gun.GunInstance
import com.chsteam.shotwithmod.network.client.MessageReloadEnd
import io.netty.buffer.ByteBuf
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class MessageReload {

    private val reloading : Boolean
    private val player : Player

    constructor(player: Player, buf: ByteBuf) {
        this.player = player
        this.reloading = buf.readBoolean()
    }

    fun handler() {
        val player = this.player
        val reloadTime = GunInstance(player, player.inventory.itemInMainHand).reloadTime
        if(this.reloading) {
            val task = object: BukkitRunnable() {
                override fun run() {
                    val reload = GunInstance(player, player.inventory.itemInMainHand).reload()

                    if(!reload) {
                        MessageReloadEnd.send(player)
                    }
                }
            }.runTaskTimer(ShotWithMod.plugin, reloadTime, reloadTime)
            reloadMap[this.player] = task
        } else {
            reloadMap[player]?.let {
                it.cancel()
            }
            reloadMap.remove(player)
        }
    }

    companion object {
        val reloadMap = hashMapOf<Player, BukkitTask>()
    }
}