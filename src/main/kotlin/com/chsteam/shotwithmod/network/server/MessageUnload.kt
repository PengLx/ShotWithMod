package com.chsteam.shotwithmod.network.server

import com.chsteam.shotwithmod.gun.GunInstance
import org.bukkit.entity.Player

object MessageUnload {
    fun unload(player: Player) {
        GunInstance(player, player.inventory.itemInMainHand).unload()
    }
}