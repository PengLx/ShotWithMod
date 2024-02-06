package com.chsteam.shotwithmod.ui

import com.chsteam.shotwithmod.attachment.Attachment
import com.chsteam.shotwithmod.attachment.Attachment.Companion.readFromGunType
import com.chsteam.shotwithmod.attachment.AttachmentType
import com.chsteam.shotwithmod.gun.GunItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.info
import taboolib.module.ui.ClickType
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Stored
import taboolib.platform.util.asLangText
import taboolib.platform.util.buildItem
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir

object UI {
    fun openAttachment(player: Player) {
        player.openMenu<Stored> {
            rows(1)
            handLocked(false)
            title = player.asLangText("attachment-ui-title")


            var currentItem : ItemStack? =  ItemStack(Material.AIR)

            onClose { e ->
                if(currentItem != null && currentItem.isNotAir()) {
                    e.player.inventory.addItem(currentItem)
                }
            }

            for(i in 1..8) {
                set(i, buildItem(Material.BLACK_STAINED_GLASS_PANE) {
                    name = ""
                    colored()
                })
            }

            fun inventoryRefresh(inventory: Inventory) {

                for(i in 1..8) {
                    set(i, buildItem(Material.BLACK_STAINED_GLASS_PANE) {
                        name = "&7"
                        colored()
                    })
                }

                if(currentItem.isNotAir() && (currentItem != null) && (GunItem.readFromItem(currentItem!!) != null)) {
                    val gun = GunItem.readFromItem(currentItem!!)!!
                    val attachmentList = gun.getAvailableAttachment()


                    attachmentList.forEach {
                        when(it) {
                            AttachmentType.SCOPE -> {
                                val item = readFromGunType(currentItem!!, AttachmentType.SCOPE)

                                if(item != null) {
                                    inventory.setItem(1, item.build())
                                } else {
                                    inventory.setItem(1, buildItem(Material.GREEN_STAINED_GLASS_PANE) {
                                        name = player.asLangText("scope-name")
                                        colored() }
                                    )
                                }
                            }
                            AttachmentType.UNDER_BARREL -> {
                                val item = readFromGunType(currentItem!!, AttachmentType.UNDER_BARREL)
                                if(item != null) {
                                    inventory.setItem(2, item.build())
                                } else {
                                    inventory.setItem(2, buildItem(Material.GREEN_STAINED_GLASS_PANE) {
                                        name = player.asLangText("under_barrel-name")
                                        colored()
                                    })
                                }
                            }
                            AttachmentType.STOCK -> {
                                val item = readFromGunType(currentItem!!, AttachmentType.STOCK)
                                if(item != null) {
                                    inventory.setItem(3, item.build())
                                } else {
                                    inventory.setItem(3, buildItem(Material.GREEN_STAINED_GLASS_PANE) {
                                        name = player.asLangText("stoke-name")
                                        colored()
                                    })
                                }
                            }
                            AttachmentType.BARREL -> {
                                val item = readFromGunType(currentItem!!, AttachmentType.BARREL)
                                if(item != null) {
                                    inventory.setItem(4, item.build())
                                } else {
                                    inventory.setItem(4, buildItem(Material.GREEN_STAINED_GLASS_PANE) {
                                        name = player.asLangText("barrel-name")
                                        colored()
                                    })
                                }
                            }
                            AttachmentType.MAGAZINE -> {
                                val item = readFromGunType(currentItem!!, AttachmentType.MAGAZINE)
                                if(item != null) {
                                    inventory.setItem(5, item.build())
                                } else {
                                    inventory.setItem(5, buildItem(Material.GREEN_STAINED_GLASS_PANE) {
                                        name = player.asLangText("magazine-name")
                                        colored()
                                    })
                                }
                            }
                        }
                    }
                }
            }

            onClick { e ->
                if(e.clickType == ClickType.DRAG && e.dragEvent().rawSlots.size > 1) {
                    e.isCancelled = true
                } else {
                    val rawSlot = if (e.clickType == ClickType.DRAG) e.dragEvent().rawSlots.firstOrNull() ?: -1 else e.rawSlot
                    when(rawSlot) {
                        0 -> {
                            e.isCancelled = true
                            val cursor = player.itemOnCursor
                            when {
                                currentItem.isAir() && cursor.isNotAir() -> {
                                    player.setItemOnCursor(null)
                                    currentItem = cursor
                                    e.inventory.setItem(rawSlot, cursor)
                                    inventoryRefresh(e.inventory)
                                }

                                currentItem.isNotAir() && cursor.isAir() -> {
                                    player.setItemOnCursor(currentItem)
                                    currentItem = ItemStack(Material.AIR)
                                    e.inventory.setItem(rawSlot, ItemStack(Material.AIR))
                                    inventoryRefresh(e.inventory)
                                }

                                currentItem.isNotAir() && currentItem.isNotAir() -> {
                                    player.setItemOnCursor(currentItem)
                                    currentItem = cursor
                                    e.inventory.setItem(rawSlot, cursor)
                                    inventoryRefresh(e.inventory)
                                }
                            }
                        }
                        1,2,3,4 -> {
                            e.isCancelled = true
                            val cursor = player.itemOnCursor
                            when {
                                //放入物品
                                e.inventory.getItem(e.rawSlot).isAir() && cursor.isNotAir() -> {
                                    val attachment = Attachment.readFromItem(cursor) ?: return@onClick
                                    var item = currentItem ?: return@onClick
                                    when(rawSlot) {
                                        1 -> {
                                            if(attachment.getType() != AttachmentType.SCOPE) {
                                               return@onClick
                                            }
                                        }
                                        2 -> {
                                            if(attachment.getType() != AttachmentType.UNDER_BARREL) {
                                                return@onClick
                                            }
                                        }
                                        3 -> {
                                            if(attachment.getType() != AttachmentType.STOCK) {
                                                return@onClick
                                            }
                                        }
                                        4 -> {
                                            if(attachment.getType() != AttachmentType.BARREL) {
                                                return@onClick
                                            }
                                        }
                                        5 -> {
                                            if(attachment.getType() != AttachmentType.MAGAZINE) {
                                                return@onClick
                                            }
                                        }
                                    }
                                    item = attachment.writeIntoGun(item)
                                    currentItem = item
                                    player.setItemOnCursor(null)
                                    e.inventory.setItem(rawSlot, cursor)
                                    e.inventory.setItem(0, item)
                                }

                                //拿走物品
                                e.inventory.getItem(e.rawSlot).isNotAir() && cursor.isAir() -> {
                                    if(currentItem == null) return@onClick
                                    var item = currentItem!!
                                    when(rawSlot) {
                                        1 -> {
                                            item = Attachment.removeFromGun(item, AttachmentType.SCOPE).first
                                        }
                                        2 -> {
                                            item = Attachment.removeFromGun(item, AttachmentType.UNDER_BARREL).first
                                        }
                                        3 -> {
                                            item = Attachment.removeFromGun(item, AttachmentType.STOCK).first
                                        }
                                        4 -> {
                                            item = Attachment.removeFromGun(item, AttachmentType.BARREL).first
                                        }
                                        5 -> {
                                            item = Attachment.removeFromGun(item, AttachmentType.MAGAZINE).first
                                        }
                                    }
                                    currentItem = item
                                    e.inventory.setItem(0, item)
                                    player.setItemOnCursor(e.inventory.getItem(e.rawSlot))
                                    inventoryRefresh(e.inventory)
                                }

                                //交换物品
                                e.inventory.getItem(e.rawSlot).isNotAir() && cursor.isNotAir() -> {
                                    if(Attachment.readFromItem(e.inventory.getItem(e.rawSlot)!!) != null) {
                                        val attachment = Attachment.readFromItem(cursor) ?: return@onClick
                                        var item = currentItem ?: return@onClick
                                        player.setItemOnCursor(e.inventory.getItem(e.rawSlot))
                                        when(rawSlot) {
                                            1 -> {
                                                if(attachment.getType() != AttachmentType.SCOPE) {
                                                    return@onClick
                                                }
                                            }
                                            2 -> {
                                                if(attachment.getType() != AttachmentType.UNDER_BARREL) {
                                                    return@onClick
                                                }
                                            }
                                            3 -> {
                                                if(attachment.getType() != AttachmentType.STOCK) {
                                                    return@onClick
                                                }
                                            }
                                            4 -> {
                                                if(attachment.getType() != AttachmentType.BARREL) {
                                                    return@onClick
                                                }
                                            }
                                            5 -> {
                                                if(attachment.getType() != AttachmentType.MAGAZINE) {
                                                    return@onClick
                                                }
                                            }
                                        }

                                        currentItem = Attachment.removeFromGun(item, attachment.getType()).first
                                        item = attachment.writeIntoGun(item)
                                        currentItem = item
                                        e.inventory.setItem(rawSlot, cursor)
                                        e.inventory.setItem(0, item)
                                    } else {
                                        val attachment = Attachment.readFromItem(cursor) ?: return@onClick
                                        var item = currentItem ?: return@onClick
                                        when(rawSlot) {
                                            1 -> {
                                                if(attachment.getType() != AttachmentType.SCOPE) {
                                                    return@onClick
                                                }
                                            }
                                            2 -> {
                                                if(attachment.getType() != AttachmentType.UNDER_BARREL) {
                                                    return@onClick
                                                }
                                            }
                                            3 -> {
                                                if(attachment.getType() != AttachmentType.STOCK) {
                                                    return@onClick
                                                }
                                            }
                                            4 -> {
                                                if(attachment.getType() != AttachmentType.BARREL) {
                                                    return@onClick
                                                }
                                            }
                                            5 -> {
                                                if(attachment.getType() != AttachmentType.MAGAZINE) {
                                                    return@onClick
                                                }
                                            }
                                        }
                                        item = attachment.writeIntoGun(item)
                                        currentItem = item
                                        player.setItemOnCursor(null)
                                        e.inventory.setItem(rawSlot, cursor)
                                        e.inventory.setItem(0, item)
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}