package com.chsteam.shotwithmod.gun

import com.chsteam.modhandler.ModHandler
import com.chsteam.modhandler.api.ModHandlerAPI
import com.chsteam.shotwithmod.ShotWithMod
import com.chsteam.shotwithmod.ammo.Ammo
import com.chsteam.shotwithmod.ammo.AmmoType
import com.chsteam.shotwithmod.api.BaseStats
import com.chsteam.shotwithmod.api.Gun
import com.chsteam.shotwithmod.api.ShotWithModAPI
import com.chsteam.shotwithmod.attachment.Attachment
import com.chsteam.shotwithmod.network.client.MessageReloadEnd
import com.chsteam.shotwithmod.network.server.MessageReload
import org.bukkit.Bukkit
import org.bukkit.SoundCategory
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.warning
import taboolib.common.util.Location
import taboolib.common.util.random
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag
import kotlin.math.cos
import kotlin.math.sin

class GunInstance : Gun {

    private val player: Player
    private val itemStack: ItemStack

    constructor(player: Player, itemStack: ItemStack) {
        this.player = player
        this.itemStack = itemStack
    }
    private var damage = 0.0
    private var speed = 0.0
    private var horizontalRandomMigration = 0f
    private var verticalRandomMigration = 0f
    private var soundDistance = 0.0
    private var maxAmmo = 0
    private var cooldown : Long = 0
    private var reloadOnce = 0
    var reloadTime : Long = 0

    override fun shot() {
        if(MessageReload.reloadMap.containsKey(this.player)) {
            MessageReloadEnd.send(this.player)
        }
        if(!checkAmmo(1)) return
        val gun = GunItem.readFromItem(this.itemStack) ?: return
        val ammo = Ammo.readFromGun(this.itemStack) ?: return
        val attachments = Attachment.readFromGun(this.itemStack)
        val type = ammo.getType()
        getGunBasic(gun)
        getAmmoStats(ammo)
        getAttachmentsStats(attachments)

        if(horizontalRandomMigration < 0) {
            horizontalRandomMigration = 0f
        }
        if(verticalRandomMigration < 0) {
            verticalRandomMigration = 0f
        }

        val randomYaw = random(-horizontalRandomMigration.toDouble(), horizontalRandomMigration.toDouble()).toFloat()
        val randomPitch = random(-verticalRandomMigration.toDouble(), verticalRandomMigration.toDouble()).toFloat()

        spawnBullet(this.player, speed.toFloat(),ammo, this.player.location.yaw + randomYaw, this.player.location.pitch + randomPitch, this)

        this.player.world.playSound(this.player.location, gun.getSound(), SoundCategory.PLAYERS, 100f, soundDistance.toFloat())
        //同步减少子弹
        submit {
            val tag = itemStack.getItemTag()
            val current = tag["AmmoCount"]?.asInt() ?: 0

            tag["AmmoCount"] = ItemTagData(current - 1)

            player.inventory.setItemInMainHand(itemStack.setItemTag(tag))
        }
    }

    private fun spawnBullet(player: Player,speed: Float,ammo: Ammo, yaw: Float, pitch: Float, gunInstance: GunInstance) {
        val loc = Location("world", 0.0, 0.0, 0.0, yaw, pitch).direction

        val damage = this.damage

        submit {
            val bullet = player.world.spawn(org.bukkit.Location(Bukkit.getWorld("world"), 0.0,500.0,0.0), Arrow::class.java)

            bullet.isSilent = true
            bullet.setGravity(false)
            bullet.shooter = player
            ShotWithModAPI.inWorldBullet[bullet] = Pair(player.entityId, Pair(ammo, gunInstance))
            ShotWithModAPI.bulletList.add(bullet.uniqueId)
            ShotWithModAPI.playerShot[bullet] = Pair(player, damage)
            bullet.teleport(player.eyeLocation)
            bullet.velocity = Vector(loc.x, loc.y, loc.z).multiply(speed)
        }
    }

    override fun shotOnAim() {
        val gun = GunItem.readFromItem(this.itemStack) ?: return
        getGunAiming(gun)
        shot()
    }

    override fun reload() : Boolean {
        val gun = GunItem.readFromItem(this.itemStack) ?: return false
        val attachments = Attachment.readFromGun(this.itemStack)
        getGunBasic(gun)
        getAttachmentsStats(attachments)
        val tag = this.itemStack.getItemTag()
        val current = tag["AmmoCount"]?.asInt() ?: 0

        if(current == this.maxAmmo) return false

        val slot = this.player.inventory.heldItemSlot + 1

        val hasAmmo = Ammo.getPairOfInventoryAmmo(this.player, gun)

        val currentAmmo = Ammo.readFromGun(this.itemStack)

        var costAmmo = this.reloadOnce

        if(hasAmmo.isEmpty()) return false

        if(current != 0) {
            hasAmmo.forEach {
                if(it.first == currentAmmo) {
                    val thisSlotAmount = this.player.inventory.getItem(it.second)?.amount ?: return@forEach
                    if(thisSlotAmount - costAmmo < 0) {
                        costAmmo = thisSlotAmount
                    }
                    if(current + costAmmo > this.maxAmmo) {
                        costAmmo -= (current + costAmmo - this.maxAmmo)
                    }
                    val ammoItem = it.first.build()
                    ammoItem.amount = thisSlotAmount - costAmmo
                    player.inventory.setItem(it.second, ammoItem)
                    tag["AmmoCount"] = ItemTagData(current + costAmmo)
                    player.inventory.setItemInMainHand(itemStack.setItemTag(tag))

                    return true
                }
            }
            return false
        }

        player.inventory.getItem(slot)?.let {item ->
            Ammo.readFromItem(item)?.let {
                if (gun.getAmmoList().contains(it)) {
                    val thisSlotAmount = item.amount
                    if(thisSlotAmount - costAmmo < 0) {
                        costAmmo = thisSlotAmount
                    }
                    if(current + costAmmo > this.maxAmmo) {
                        costAmmo -= (current + costAmmo - this.maxAmmo)
                    }
                    item.amount = thisSlotAmount - costAmmo

                    player.inventory.setItem(slot, item)
                    tag["AmmoCount"] = ItemTagData(current + costAmmo)
                    player.inventory.setItemInMainHand(it.writeIntoGun(itemStack.setItemTag(tag)))
                    return true
                }
            }
        }

        hasAmmo.forEach {
            if(it.first == currentAmmo) {
                val thisSlotAmount = this.player.inventory.getItem(it.second)?.amount ?: return@forEach
                if(thisSlotAmount - costAmmo < 0) {
                    costAmmo = thisSlotAmount
                }
                if(current + costAmmo > this.maxAmmo) {
                    costAmmo -= (current + costAmmo - this.maxAmmo)
                }
                val ammoItem = it.first.build()
                ammoItem.amount = thisSlotAmount - costAmmo
                player.inventory.setItem(it.second, ammoItem)
                tag["AmmoCount"] = ItemTagData(current + costAmmo)
                player.inventory.setItemInMainHand(itemStack.setItemTag(tag))

                return true
            }
        }

        hasAmmo.first().let {
            val thisSlotAmount = this.player.inventory.getItem(it.second)?.amount ?: return@let
            if(thisSlotAmount - costAmmo < 0) {
                costAmmo = thisSlotAmount
            }
            if(current + costAmmo > this.maxAmmo) {
                costAmmo -= (current + costAmmo - this.maxAmmo)
            }
            val ammoItem = it.first.build()
            ammoItem.amount = thisSlotAmount - costAmmo
            player.inventory.setItem(it.second, ammoItem)
            tag["AmmoCount"] = ItemTagData(current + costAmmo)
            player.inventory.setItemInMainHand(it.first.writeIntoGun(itemStack.setItemTag(tag)))

            return true
        }

        return false
    }

    override fun unload() {
        val tag = this.itemStack.getItemTag()
        val current = tag["AmmoCount"]?.asInt() ?: return

        val ammo = Ammo.readFromGun(itemStack)?.build() ?: return

        tag["AmmoCount"] = ItemTagData(0)

        tag["SWM_AmmoID"] = ItemTagData("NULL")

        ammo.amount = current

        this.player.inventory.setItemInMainHand(itemStack.setItemTag(tag))
        this.player.inventory.addItem(ammo)
    }

    fun getItem() : ItemStack {
        return this.itemStack
    }

    private fun checkAmmo(amount: Int) : Boolean{
        val tag = itemStack.getItemTag()
        val current = tag["AmmoCount"]?.asInt() ?: 0

        return current - amount >= 0
    }


    private fun getGunBasic(gun : GunItem) {
        gun.getCommonModifyList().forEach {
            when(it.key) {
                BaseStats.DAMAGE -> {
                    this.damage += it.value.asDouble()
                }
                BaseStats.SPEED -> {
                    this.speed += it.value.asDouble()
                }
                BaseStats.Horizontal_Random_Migration -> {
                    this.horizontalRandomMigration += it.value.asFloat()
                }
                BaseStats.Vertical_Random_Migration -> {
                    this.verticalRandomMigration += it.value.asFloat()
                }
                BaseStats.SOUND_DISTANCE -> {
                    this.soundDistance += it.value.asDouble()
                }
                BaseStats.MAX_AMMO -> {
                    this.maxAmmo += it.value.asInt()
                }
                BaseStats.COOLDOWN -> {
                    this.cooldown += it.value.asLong()
                }
                BaseStats.ONCE_RELOAD -> {
                    this.reloadOnce += it.value.asInt()
                }
                BaseStats.RELOAD_TIME -> {
                    this.reloadTime += it.value.asLong()
                }
                else -> {}
            }
        }
    }

    private fun getGunAiming(gun : GunItem) {
        gun.getAimModifyList().forEach {
            when(it.key) {
                BaseStats.DAMAGE -> {
                    this.damage += it.value.asDouble()
                }
                BaseStats.SPEED -> {
                    this.speed += it.value.asDouble()
                }
                BaseStats.Horizontal_Random_Migration -> {
                    this.horizontalRandomMigration += it.value.asFloat()
                }
                BaseStats.Vertical_Random_Migration -> {
                    this.verticalRandomMigration += it.value.asFloat()
                }
                BaseStats.SOUND_DISTANCE -> {
                    this.soundDistance += it.value.asDouble()
                }
                BaseStats.MAX_AMMO -> {
                    this.maxAmmo += it.value.asInt()
                }
                BaseStats.COOLDOWN -> {
                    this.cooldown += it.value.asLong()
                }
                BaseStats.ONCE_RELOAD -> {
                    this.reloadOnce += it.value.asInt()
                }
                BaseStats.RELOAD_TIME -> {
                    this.reloadTime += it.value.asLong()
                }
                else -> {}
            }
        }
    }

    private fun getAmmoStats(ammo: Ammo) {
        ammo.getModifyList().forEach {
            when(it.key) {
                BaseStats.DAMAGE -> {
                    this.damage += it.value.asDouble()
                }
                BaseStats.SPEED -> {
                    this.speed += it.value.asDouble()
                }
                BaseStats.Horizontal_Random_Migration -> {
                    this.horizontalRandomMigration += it.value.asFloat()
                }
                BaseStats.Vertical_Random_Migration -> {
                    this.verticalRandomMigration += it.value.asFloat()
                }
                BaseStats.SOUND_DISTANCE -> {
                    this.soundDistance += it.value.asDouble()
                }
                BaseStats.MAX_AMMO -> {
                    this.maxAmmo += it.value.asInt()
                }
                BaseStats.COOLDOWN -> {
                    this.cooldown += it.value.asLong()
                }
                BaseStats.ONCE_RELOAD -> {
                    this.reloadOnce += it.value.asInt()
                }
                BaseStats.RELOAD_TIME -> {
                    this.reloadTime += it.value.asLong()
                }
                else -> {}
            }
        }
    }

    private fun getAttachmentsStats(attachments: List<Attachment>) {
        attachments.forEach { attachment ->
            attachment.getModifyList().forEach {
                when(it.key) {
                    BaseStats.DAMAGE -> {
                        this.damage += it.value.asDouble()
                    }
                    BaseStats.SPEED -> {
                        this.speed += it.value.asDouble()
                    }
                    BaseStats.Horizontal_Random_Migration -> {
                        this.horizontalRandomMigration += it.value.asFloat()
                    }
                    BaseStats.Vertical_Random_Migration -> {
                        this.verticalRandomMigration += it.value.asFloat()
                    }
                    BaseStats.SOUND_DISTANCE -> {
                        this.soundDistance += it.value.asDouble()
                    }
                    BaseStats.MAX_AMMO -> {
                        this.maxAmmo += it.value.asInt()
                    }
                    BaseStats.COOLDOWN -> {
                        this.cooldown += it.value.asLong()
                    }
                    BaseStats.ONCE_RELOAD -> {
                        this.reloadOnce += it.value.asInt()
                    }
                    BaseStats.RELOAD_TIME -> {
                        this.reloadTime += it.value.asLong()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun checkTime() : Boolean {
        val time = System.currentTimeMillis()
        if(!shotTime.containsKey(player)) {
            shotTime[player] = time
            return true
        } else {
            val lastTime = shotTime[player] ?: return false

            return lastTime - time >= this.cooldown
        }
    }

    companion object {
        private val shotTime = hashMapOf<Player, Long>()
    }
}