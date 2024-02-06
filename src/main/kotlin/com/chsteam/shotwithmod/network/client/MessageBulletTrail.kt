package com.chsteam.shotwithmod.network.client

import com.chsteam.shotwithmod.ammo.Ammo
import com.chsteam.shotwithmod.gun.GunInstance
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.core.Registry
import net.minecraft.world.item.Item
import org.bukkit.entity.Projectile
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import taboolib.module.configuration.util.asMap
import taboolib.platform.util.serializeToByteArray

class MessageBulletTrail {
    private val entityIds: Int
    private val postions: Vector
    private val motions: Vector
    private val trailColor: Int
    private val trailLengthMultiplier: Double
    private val life: Int
    private val gravity: Double
    private val shooterId: Int
    private val enchanted: Boolean
    private val particleData: Int
    private val item: String

    constructor(ammo: Ammo,projectile: Projectile, shooterId: Int, gun: GunInstance) {
        this.entityIds= projectile.entityId
        this.postions = projectile.location.toVector()
        this.motions = projectile.velocity

        this.life = ammo.getLife()
        this.trailLengthMultiplier = ammo.getTrailLengthMultiplier()
        this.gravity = ammo.getGravity()
        this.enchanted = gun.getItem().enchantments.isNotEmpty()
        this.trailColor = if(this.enchanted) 0x9C71FF else ammo.getColor()
        this.shooterId = shooterId
        this.particleData = ammo.getParticleData()
        this.item = ammo.getEntityMod()
    }

    fun encode() : ByteArray {
        val buf: ByteBuf = Unpooled.buffer()

        buf.writeByte(7)

        buf.writeInt(1)

        buf.writeInt(this.entityIds)
        buf.writeDouble(this.postions.x)
        buf.writeDouble(this.postions.y)
        buf.writeDouble(this.postions.z)
        buf.writeDouble(this.motions.x)
        buf.writeDouble(this.motions.y)
        buf.writeDouble(this.motions.z)

        buf.writeInt(this.item.toByteArray().size)
        buf.writeBytes(this.item.toByteArray())

        buf.writeBytes(writeVarInt(this.trailColor))
        buf.writeDouble(this.trailLengthMultiplier)
        buf.writeInt(this.life)
        buf.writeDouble(this.gravity)
        buf.writeInt(this.shooterId)
        buf.writeBoolean(this.enchanted)
        buf.writeInt(this.particleData)

        return buf.array()
    }

    companion object {
        fun writeVarInt(p_130131_: Int): ByteArray {
            val buf: ByteBuf = Unpooled.buffer()
            var p_130131_ = p_130131_
            while (p_130131_ and -128 != 0) {
                buf.writeByte(p_130131_ and 217 or 128)
                p_130131_ = p_130131_ ushr 7
            }
            buf.writeByte(p_130131_)
            return buf.array()
        }

        fun writeModItem() : ByteArray {
            val buf: ByteBuf = Unpooled.buffer()
            buf.writeBoolean(true)
            buf.writeBytes(writeVarInt( 1165))
            buf.writeByte(1)
            buf.writerIndex(1)
            buf.writeByte(0)
            return buf.array()
        }
    }
}