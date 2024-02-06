package com.chsteam.shotwithmod.attachment

import com.chsteam.shotwithmod.api.BaseStats
import com.chsteam.shotwithmod.api.ShotWithModAPI
import org.bukkit.Material
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Configuration.Companion.loadFromFile
import taboolib.module.nms.ItemTagData
import java.io.File

object AttachmentLoader {
    fun load() {
        val file = File(getDataFolder(),"attachments")
        if(!file.exists()) {
            releaseResourceFile("attachments/exampleAttachment.yml", true)
        }
        val attachments = load(file)
        ShotWithModAPI.attachments.clear()
        attachments.forEach {
            ShotWithModAPI.attachments[it.getId()] = it
        }
    }

    private fun load(file: File) : List<Attachment> {
        return when {
            file.isDirectory -> file.listFiles()?.flatMap { load(it) }?.toList() ?: emptyList()
            file.name.endsWith(".yml") -> load(loadFromFile(file))
            else ->  emptyList()
        }
    }

    private fun load(file: Configuration) : List<Attachment>{
        return file.getKeys(false).map {
            load(it, file.getConfigurationSection(it)!!)
        }
    }

    private fun load(key: String, root: ConfigurationSection) : Attachment {
        val modId = root.getString("modId") ?: "silencer"
        val namespace = root.getString("namespace") ?: "cgm"
        val name = root.getString("name") ?: "配件"
        val lore = root.getStringList("lore")
        val material = Material.getMaterial(root.getString("material")?.uppercase() ?: "DIAMOND") ?: Material.DIAMOND
        val modifyList = hashMapOf<BaseStats, ItemTagData>()

        val modify = root.getConfigurationSection("stats")

        var type = AttachmentType.SCOPE

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

                type = AttachmentType.valueOf(root.getString("type")?.uppercase() ?: "SCOPE")
            }
            catch (e : IllegalArgumentException) {
                e.printStackTrace()
            }
        }

        return Attachment(key, modId, namespace, modifyList, type, material, name, lore)
    }

}