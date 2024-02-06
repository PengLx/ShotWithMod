package com.chsteam.shotwithmod.listener

import com.chsteam.shotwithmod.api.ShotWithModAPI
import com.chsteam.shotwithmod.listener.PacketListener.trackMap
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.projectiles.ProjectileSource
import taboolib.common.platform.event.SubscribeEvent

object ProjectileListener {
    @SubscribeEvent
    fun e(e: ProjectileLaunchEvent) {
        if(e.isCancelled) {
            if(ShotWithModAPI.inWorldBullet.containsKey(e.entity)) {
                ShotWithModAPI.inWorldBullet.remove(e.entity)
                ShotWithModAPI.bulletList.remove(e.entity.uniqueId)
            }
        }
    }

    @SubscribeEvent
    fun e(e: ProjectileHitEvent) {
        if(ShotWithModAPI.inWorldBullet.containsKey(e.entity)) {
            ShotWithModAPI.inWorldBullet.remove(e.entity)
            ShotWithModAPI.bulletList.remove(e.entity.uniqueId)

            e.hitEntity?.let { aim ->
                ShotWithModAPI.playerShot[e.entity]?.let {
                    (aim as LivingEntity).damage(it.second, it.first)
                }

            }
            e.entity.remove()
            trackMap.remove(e.entity.uniqueId)

            e.isCancelled = true
        }
    }
}