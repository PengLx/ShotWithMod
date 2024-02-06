package com.chsteam.shotwithmod.ammo

import com.chsteam.shotwithmod.api.BaseStats
import com.chsteam.shotwithmod.api.ShotWithModAPI
import org.bukkit.Material
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.nms.ItemTagData
import java.io.File

object AmmoLoader {
    fun load() {
        val file = File(getDataFolder(),"ammo")
        if(!file.exists()) {
            releaseResourceFile("ammo/common.yml", true)
        }
        val ammo = load(file)
        ShotWithModAPI.ammo.clear()
        ammo.forEach {
            ShotWithModAPI.ammo[it.getId()] = it
        }
    }

    private fun load(file: File) : List<Ammo> {
        return when {
            file.isDirectory -> file.listFiles()?.flatMap { load(it) }?.toList() ?: emptyList()
            file.name.endsWith(".yml") -> load(Configuration.loadFromFile(file))
            else ->  emptyList()
        }
    }

    private fun load(file: Configuration) : List<Ammo>{
        return file.getKeys(false).map {
            load(it, file.getConfigurationSection(it)!!)
        }
    }

    private fun load(key: String, root: ConfigurationSection) : Ammo {
        val modId = root.getString("modId") ?: "basic_ammo"
        val namespace = root.getString("namespace") ?: "cgm"
        val name = root.getString("name") ?: "子弹"
        val lore = root.getStringList("lore")
        val material = Material.getMaterial(root.getString("material")?.uppercase() ?: "DIAMOND") ?: Material.DIAMOND
        val color = root.getInt("color")
        val gravity = root.getDouble("gravity")
        val particleData = root.getInt("particleData")
        val trailLengthMultiplier = root.getDouble("trailLengthMultiplier")
        val life = root.getInt("life")
        val modifyList = hashMapOf<BaseStats, ItemTagData>()

        val modify = root.getConfigurationSection("stats")

        var type = AmmoType.COMMON

        modify?.getKeys(false)?.map {

            try {
                val baseStats = BaseStats.valueOf(it)

                when(baseStats.type) {
                    Int -> {
                        modifyList[baseStats] = ItemTagData(modify.getInt(it))
                    }
                    Double -> {
                        modifyList[baseStats] = ItemTagData(modify.getDouble(it))
                    }
                    Float -> {
                        modifyList[baseStats] = ItemTagData(modify.getDouble(it).toFloat())
                    }
                    Long -> {
                        modifyList[baseStats] = ItemTagData(modify.getLong(it))
                    }
                }

                type = AmmoType.valueOf(root.getString("type")?.uppercase() ?: "SCOPE")
            }
            catch (e : IllegalArgumentException) {
                e.printStackTrace()
            }
        }

        return Ammo(key, modId, namespace, modifyList, type, material, life , trailLengthMultiplier, color, gravity, particleData, name, lore)
    }
}