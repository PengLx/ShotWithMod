package com.chsteam.shotwithmod.listener

import com.chsteam.modhandler.api.ModHandlerAPI
import com.chsteam.shotwithmod.ShotWithMod
import com.chsteam.shotwithmod.ShotWithMod.CHANNEL
import com.chsteam.shotwithmod.ShotWithMod.MOD_ID
import com.chsteam.shotwithmod.api.ShotWithModAPI
import com.chsteam.shotwithmod.network.client.MessageBulletTrail
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.module.nms.PacketSendEvent
import taboolib.module.nms.sendPacket
import java.util.UUID

object PacketListener {
    val trackMap = hashMapOf<UUID, MutableList<Player>>()
    @SubscribeEvent
    fun e(e: PacketSendEvent) {
        if(e.packet.name == "PacketPlayOutSpawnEntity") {
            val UUID = e.packet.read<UUID>("uuid")?: return
            if(!ShotWithModAPI.bulletList.contains(UUID)) return
            if (trackMap[UUID] == null) {
                trackMap[UUID] = mutableListOf()
            }

            trackMap[UUID]?.add(e.player)

            e.isCancelled = true
            submit {
                val entity = Bukkit.getEntity(UUID)

                if(entity is Projectile) {
                    val info = ShotWithModAPI.inWorldBullet[entity] ?: return@submit

                    if(info.second.first.getEntityMod() == "grenade") {
                        ModHandlerAPI.setEntityModel(e.player, entity, "grenade", "cgm")
                    } else {
                        e.isCancelled = true
                        e.player.sendPluginMessage(ShotWithMod.plugin, "$MOD_ID:$CHANNEL", MessageBulletTrail(info.second.first, entity, info.first, info.second.second).encode())
                    }
                }
            }
        }
    }

}