package com.chsteam.shotwithmod

import com.chsteam.shotwithmod.ammo.AmmoLoader
import com.chsteam.shotwithmod.api.Gun
import com.chsteam.shotwithmod.attachment.AttachmentLoader
import com.chsteam.shotwithmod.attachment.AttachmentType
import com.chsteam.shotwithmod.gun.GunLoader
import com.chsteam.shotwithmod.network.ModPacketListener
import org.bukkit.Bukkit
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.platform.BukkitPlugin

object ShotWithMod : Plugin() {

    val plugin by lazy {
        BukkitPlugin.getInstance()
    }

    const val MOD_ID = "cgm"

    const val CHANNEL = "play"

    override fun onEnable() {

        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "$MOD_ID:$CHANNEL", ModPacketListener)
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "$MOD_ID:$CHANNEL")

        reload()
        info("Successfully running ShotWithMod!")
    }

    fun reload() {
        AmmoLoader.load()
        AttachmentLoader.load()
        GunLoader.load()
    }
}