package com.chsteam.shotwithmod.ammo

import com.chsteam.modhandler.api.ModItem
import com.chsteam.shotwithmod.api.BaseStats
import com.chsteam.shotwithmod.api.ShotWithModAPI
import com.chsteam.shotwithmod.gun.GunItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag
import java.util.DoubleSummaryStatistics

class Ammo : ModItem {
    private val id: String

    private val modifyList : HashMap<BaseStats, ItemTagData>

    private val type : AmmoType

    private val life: Int

    private val trailLengthMultiplier: Double

    private val color : Int

    private val gravity: Double

    private val particleData : Int

    private val entityString: String

    constructor(id: String,
                modId: String,
                namespace: String,
                modifyList: HashMap<BaseStats, ItemTagData>,
                type: AmmoType,
                material: Material,
                life: Int,
                trailLengthMultiplier: Double,
                color: Int,
                gravity: Double,
                particleData: Int,
                name: String,
                lore: List<String>
    ) : super(modId, namespace, material, name, lore) {
        this.id = id
        this.modifyList = modifyList
        this.type = type
        this.life = life
        this.trailLengthMultiplier = trailLengthMultiplier
        this.color = color
        this.gravity = gravity
        this.particleData = particleData
        this.entityString = modId
    }

    fun getId() : String {
        return this.id
    }

    fun getEntityMod() : String {
        return this.entityString
    }

    fun getModifyList() : HashMap<BaseStats, ItemTagData> {
        return this.modifyList
    }

    fun getType() : AmmoType {
        return this.type
    }

    fun getGravity() : Double {
        return this.gravity
    }

    fun getColor() : Int {
        return this.color
    }

    fun getTrailLengthMultiplier() : Double {
        return this.trailLengthMultiplier
    }

    fun getLife(): Int {
        return this.life
    }

    fun getParticleData(): Int {
        return this.particleData
    }


    override fun build(): ItemStack {
        val item = super.build()
        val tag = item.getItemTag()

        tag["SWMID_AMMO"] = ItemTagData(this.id)
        return item.setItemTag(tag)
    }

    fun writeIntoGun(itemStack: ItemStack) : ItemStack {
        val tag = itemStack.getItemTag()

        tag["SWM_AmmoID"] = ItemTagData(this.id)

        return itemStack.setItemTag(tag)
    }

    companion object {
        fun readFromItem(itemStack: ItemStack) : Ammo? {
            val id = itemStack.getItemTag()["SWMID_AMMO"]?.asString() ?: return null
            return ShotWithModAPI.ammo[id]
        }


        fun readFromGun(itemStack: ItemStack) : Ammo? {
            val tag = itemStack.getItemTag()
            val id = itemStack.getItemTag()["SWM_AmmoID"]?.asString() ?: return null
            return ShotWithModAPI.ammo[id]
        }

        fun getPairOfInventoryAmmo(player: Player,gun: GunItem) : List<Pair<Ammo, Int> >{

            val list = mutableListOf<Pair<Ammo, Int>>()
            for(i in 0..36) {
                val item = player.inventory.getItem(i)

                item?.let {item ->
                    val ammo = readFromItem(item)

                    ammo?.let {
                        if(gun.getAmmoList().contains(it)) {
                            list.add(Pair(it, i))
                        }
                    }
                }
            }

            return list
        }
    }
}