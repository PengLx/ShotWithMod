package com.chsteam.shotwithmod.gun

import com.chsteam.modhandler.api.ModItem
import com.chsteam.shotwithmod.ammo.Ammo
import com.chsteam.shotwithmod.ammo.AmmoType
import com.chsteam.shotwithmod.api.BaseStats
import com.chsteam.shotwithmod.api.ShotWithModAPI
import com.chsteam.shotwithmod.attachment.AttachmentType
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

class GunItem : ModItem{

    private val id: String

    private val commonModifyList : HashMap<BaseStats, ItemTagData>

    private val aimModifyList : HashMap<BaseStats, ItemTagData>

    private val ammoList : List<Ammo>

    private val enableAttachment : List<AttachmentType>

    private val sound : String

    constructor(id: String,
                modId: String,
                namespace: String,
                commonModifyList: HashMap<BaseStats, ItemTagData>,
                aimModifyList: HashMap<BaseStats, ItemTagData>,
                ammoList: List<Ammo>,
                enableAttachment : List<AttachmentType>,
                material: Material,
                sound: String,
                name: String,
                lore: List<String>,
    ) : super(modId, namespace, material, name, lore) {
        this.id = id
        this.commonModifyList = commonModifyList
        this.aimModifyList = aimModifyList
        this.ammoList = ammoList
        this.enableAttachment = enableAttachment
        this.sound = sound
    }

    fun getId() : String {
        return this.id
    }

    fun getAmmoList(): List<Ammo> {
        return this.ammoList;
    }

    fun getCommonModifyList(): HashMap<BaseStats, ItemTagData> {
        return this.commonModifyList
    }

    fun getAimModifyList(): HashMap<BaseStats, ItemTagData> {
        return this.aimModifyList
    }

    fun getSound(): String {
        return this.sound
    }

    fun getAvailableAttachment() : List<AttachmentType> {
        return this.enableAttachment
    }

    override fun build(): ItemStack {
        val item = super.build()
        val tag = item.getItemTag()

        tag["SWMID_GUN"] = ItemTagData(this.id)
        tag["MAX_AMMO"] = this.commonModifyList[BaseStats.MAX_AMMO]
        tag["COOLDOWN"] = this.commonModifyList[BaseStats.COOLDOWN]
        return item.setItemTag(tag)
    }

    companion object {
        fun readFromItem(itemStack: ItemStack) : GunItem? {
            val id = itemStack.getItemTag()["SWMID_GUN"]?.asString() ?: return null
            return ShotWithModAPI.guns[id]
        }

        fun setColorValue(item: ItemStack, value: Int) : ItemStack {
            val tag = item.getItemTag()

            tag["SWMID_GUN"] = ItemTagData(value)
            return item.setItemTag(tag)
        }
    }
}