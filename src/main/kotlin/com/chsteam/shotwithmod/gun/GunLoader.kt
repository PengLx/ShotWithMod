package com.chsteam.shotwithmod.gun

import com.chsteam.shotwithmod.ammo.Ammo
import com.chsteam.shotwithmod.ammo.AmmoType
import com.chsteam.shotwithmod.api.BaseStats
import com.chsteam.shotwithmod.api.ShotWithModAPI
import com.chsteam.shotwithmod.attachment.AttachmentType
import org.bukkit.Material
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.nms.ItemTagData
import java.io.File

object GunLoader {
    fun load() {
        val file = File(getDataFolder(),"guns")
        if(!file.exists()) {
            releaseResourceFile("guns/pistol.yml", true)
        }
        val guns = load(file)
        ShotWithModAPI.guns.clear()

        guns.forEach {
            ShotWithModAPI.guns[it.getId()] = it
        }
    }

    private fun load(file: File) : List<GunItem> {
        return when {
            file.isDirectory -> file.listFiles()?.flatMap { load(it) }?.toList() ?: emptyList()
            file.name.endsWith(".yml") -> load(Configuration.loadFromFile(file))
            else ->  emptyList()
        }
    }

    private fun load(file: Configuration) : List<GunItem>{
        return file.getKeys(false).map {
            load(it, file.getConfigurationSection(it)!!)
        }
    }

    private fun load(key: String, root: ConfigurationSection) : GunItem {
        val modId = root.getString("modId") ?: "pistol"
        val namespace = root.getString("namespace") ?: "cgm"
        val name = root.getString("name") ?: "枪"
        val lore = root.getStringList("lore")
        val sound = root.getString("sound") ?: "assult_rifle.fire"
        val material = Material.getMaterial(root.getString("material")?.uppercase() ?: "DIAMOND") ?: Material.DIAMOND
        val commonModifyList = hashMapOf<BaseStats, ItemTagData>()
        val aimModifyList = hashMapOf<BaseStats, ItemTagData>()

        val ammoList = mutableListOf<Ammo>()

        root.getStringList("ammo-list").forEach {
            ShotWithModAPI.ammo[it]?.let { it1 -> ammoList.add(it1) }
        }

        val enableAttachment = mutableListOf<AttachmentType>()

        root.getStringList("enable-attachment").forEach {
            enableAttachment.add(AttachmentType.valueOf(it.uppercase()))
        }

        val modify = root.getConfigurationSection("stats")

        modify?.getKeys(false)?.map {

            try {
                val baseStats = BaseStats.valueOf(it)

                when(baseStats.type) {
                    Int -> {
                        commonModifyList[baseStats] = ItemTagData(modify.getInt(it))
                    }
                    Double -> {
                        commonModifyList[baseStats] = ItemTagData(modify.getDouble(it))
                    }
                    Float -> {
                        commonModifyList[baseStats] = ItemTagData(modify.getDouble(it).toFloat())
                    }
                    Long -> {
                        commonModifyList[baseStats] = ItemTagData(modify.getLong(it))
                    }
                }
            }
            catch (e : IllegalArgumentException) {
                e.printStackTrace()
            }
        }

        val aimModify =  root.getConfigurationSection("ShotOnAim.stats")

        aimModify?.getKeys(false)?.map {
            try {
                val baseStats = BaseStats.valueOf(it)

                when(baseStats.type) {
                    Int -> {
                        aimModifyList[baseStats] = ItemTagData(aimModify.getInt(it))
                    }
                    Double -> {
                        aimModifyList[baseStats] = ItemTagData(aimModify.getDouble(it))
                    }
                    Float -> {
                        aimModifyList[baseStats] = ItemTagData(aimModify.getDouble(it).toFloat())
                    }
                    Long -> {
                        commonModifyList[baseStats] = ItemTagData(aimModify.getLong(it))
                    }
                }
            }
            catch (e : IllegalArgumentException) {
                e.printStackTrace()
            }
        }
        return GunItem(key, modId, namespace, commonModifyList, aimModifyList, ammoList, enableAttachment, material, sound, name, lore)
    }
}