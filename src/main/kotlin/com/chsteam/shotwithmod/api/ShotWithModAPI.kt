package com.chsteam.shotwithmod.api

import com.chsteam.shotwithmod.ammo.Ammo
import com.chsteam.shotwithmod.attachment.Attachment
import com.chsteam.shotwithmod.gun.GunInstance
import com.chsteam.shotwithmod.gun.GunItem
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayOutputStream
import java.util.UUID

object ShotWithModAPI {
    val aimingPlayer = hashMapOf<Player, Boolean>()

    val shootingPlayer = hashMapOf<Player, Boolean>()

    val attachments = hashMapOf<String, Attachment>()

    val ammo = hashMapOf<String, Ammo>()

    val guns = hashMapOf<String, GunItem>()

    val playerShot = hashMapOf<Projectile, Pair<Player, Double>>()

    val inWorldBullet = hashMapOf<Projectile, Pair<Int, Pair<Ammo, GunInstance>>>()

    val bulletList = mutableListOf<UUID>()

    fun fromItemStack(itemStack: ItemStack): ByteArray {
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            BukkitObjectOutputStream(byteArrayOutputStream).use { bukkitObjectOutputStream ->
                bukkitObjectOutputStream.writeObject(itemStack)
                return byteArrayOutputStream.toByteArray()
            }
        }
    }
}