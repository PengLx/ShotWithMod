package com.chsteam.shotwithmod.attachment

import com.chsteam.modhandler.api.ModItem
import com.chsteam.shotwithmod.api.BaseStats
import com.chsteam.shotwithmod.api.ShotWithModAPI
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.*

class Attachment : ModItem {

    private val id : String

    private val modifyList : HashMap<BaseStats, ItemTagData>

    private val type : AttachmentType

    private val thisMod: String

    private val thisNS: String

    constructor(id: String,
                modID: String,
                nameSpace: String,
                modifyList: HashMap<BaseStats, ItemTagData>,
                attachmentType: AttachmentType,
                material: Material,
                name: String,
                lore: List<String>
    )
            : super(modID, nameSpace, material,name, lore) {
        this.id = id
        this.modifyList = modifyList
        this.type = attachmentType

        this.thisMod = modID
        this.thisNS = nameSpace
    }

    fun getId() : String {
        return this.id
    }

    fun getModifyList() : HashMap<BaseStats, ItemTagData> {
        return this.modifyList
    }

    fun getType() : AttachmentType {
        return this.type
    }

    override fun build() : ItemStack {
        val item = super.build()
        val tag = item.getItemTag()

        tag["SWMID_ATTACH"] = ItemTagData(this.id)

        return item.setItemTag(tag)
    }

    fun writeIntoGun(itemStack: ItemStack) : ItemStack {
        val tag = itemStack.getItemTag()

        tag["SWM_AttachmentType_${this.type.name}"] = ItemTagData(this.id)

        if (this.modifyList.containsKey(BaseStats.MAX_AMMO)) {
            tag["MAX_AMMO"] = ItemTagData(tag["MAX_AMMO"]!!.asInt() + this.modifyList[BaseStats.MAX_AMMO]!!.asInt())
        }

        if (this.modifyList.containsKey(BaseStats.COOLDOWN)) {
            tag["COOLDOWN"] = ItemTagData(tag["COOLDOWN"]!!.asInt() + this.modifyList[BaseStats.COOLDOWN]!!.asInt())
        }

        if(tag["Attachments"] == null) {
            tag["Attachments"] = ItemTagData(ItemTag())
        }

        val subTag = ItemTag()
        subTag["id"] = ItemTagData("${this.thisNS}:${this.thisMod}")
        subTag["Count"] = ItemTagData(1)

        when(this.type) {
            AttachmentType.SCOPE -> {
                tag["Attachments"]!!.asCompound()["Scope"] = subTag
            }

            AttachmentType.BARREL -> {
                tag["Attachments"]!!.asCompound()["Barrel"] = subTag
            }
            AttachmentType.STOCK -> {
                tag["Attachments"]!!.asCompound()["Stoke"] = subTag
            }

            AttachmentType.UNDER_BARREL -> {
                tag["Attachments"]!!.asCompound()["Under_Barrel"] = subTag
            }
        }

        return itemStack.setItemTag(tag)
    }

    companion object {
        fun removeFromGun(itemStack: ItemStack, attachmentType: AttachmentType) : Pair<ItemStack, ItemStack?> {
            val tag = itemStack.getItemTag()
            val id = tag["SWM_AttachmentType_${attachmentType.name}"]?.asString()

            tag["SWM_AttachmentType_${attachmentType.name}"] = null

            val pair = Pair(itemStack, null)

            id?.let {

                when(attachmentType) {
                    AttachmentType.SCOPE -> {
                        tag["Attachments"]!!.asCompound()["Scope"] = ItemTag()
                    }

                    AttachmentType.BARREL -> {
                        tag["Attachments"]!!.asCompound()["Barrel"] = ItemTag()
                    }
                    AttachmentType.STOCK -> {
                        tag["Attachments"]!!.asCompound()["Stoke"] = ItemTag()
                    }

                    AttachmentType.UNDER_BARREL -> {
                        tag["Attachments"]!!.asCompound()["Under_Barrel"] = ItemTag()
                    }
                }


                return Pair(itemStack.setItemTag(tag), ShotWithModAPI.attachments[it]?.build())
            }

            return pair
        }

        fun readFromItem(itemStack: ItemStack) : Attachment? {
            val id = itemStack.getItemTag()["SWMID_ATTACH"]?.asString() ?: return null
            return ShotWithModAPI.attachments[id]
        }

        fun readFromGun(itemStack: ItemStack) : List<Attachment> {
            val list = mutableListOf<Attachment>()
            val tag = itemStack.getItemTag()
            AttachmentType.values().forEach {
                val id = tag["SWM_AttachmentType_${it.name}"]?.asString()
                id?.let {
                    ShotWithModAPI.attachments[it]?.let { it1 -> list.add(it1) }
                }
            }

            return list
        }

        fun readFromGunType(itemStack: ItemStack, attachmentType: AttachmentType) : Attachment? {
            val tag = itemStack.getItemTag()
            val id = tag["SWM_AttachmentType_${attachmentType.name}"]?.asString()
            id?.let {
                return ShotWithModAPI.attachments[it]
            }

            return null
        }

    }

}