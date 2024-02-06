package com.chsteam.shotwithmod.command

import com.chsteam.shotwithmod.ShotWithMod
import com.chsteam.shotwithmod.api.ShotWithModAPI
import com.chsteam.shotwithmod.gun.GunInstance
import com.chsteam.shotwithmod.gun.GunItem
import com.chsteam.shotwithmod.ui.UI
import net.minecraft.commands.ICompletionProvider.suggest
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper

@CommandHeader("shotwithmod")
object Command {
    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val reload = subCommand {
        execute<CommandSender> { _, _, _ ->
            ShotWithMod.reload()
        }
    }

    @CommandBody
    val getGun = subCommand {
        dynamic(comment = "gun") {
            suggest {
                ShotWithModAPI.guns.values.map { it.getId() }
            }
            execute<Player> { sender, context, _ ->
                ShotWithModAPI.guns[context.argument(0)]?.let {
                    sender.inventory.addItem(it.build())
                }
            }
        }
    }

    @CommandBody
    val getAttachment= subCommand {
        dynamic(comment = "attachment") {
            suggest {
                ShotWithModAPI.attachments.values.map { it.getId() }
            }
            execute<Player> { sender, context, _ ->
                ShotWithModAPI.attachments[context.argument(0)]?.let {
                    sender.inventory.addItem(it.build())
                }
            }
        }
    }


    @CommandBody
    val getAmmo = subCommand {
        dynamic(comment = "ammo") {
            suggest {
                ShotWithModAPI.ammo.values.map { it.getId() }
            }
            dynamic(comment = "amount") {
                execute<Player> { sender, context, _ ->
                    ShotWithModAPI.ammo[context.argument(-1)]?.let {
                        val item = it.build()
                        item.amount = context.argument(0).toInt()
                        sender.inventory.addItem(item)
                    }
                }
            }
        }
    }

    @CommandBody
    val openAttachmentUI = subCommand {
        dynamic(comment = "player") {
            suggest {
                Bukkit.getOnlinePlayers().map { it.name }
            }
            execute<CommandSender> { _, context, _ ->
                Bukkit.getPlayer(context.argument(0))?.let { UI.openAttachment(it) }
            }
        }
        execute<Player> { sender, _, _ ->
            UI.openAttachment(sender)
        }
    }

    @CommandBody
    val addAttachment = subCommand {
        dynamic(comment = "attachment") {
            suggest {
                ShotWithModAPI.attachments.values.map { it.getId() }
            }
            execute<Player> { sender, context, _ ->
                val item = sender.inventory.itemInMainHand

                if(GunItem.readFromItem(item) != null) {
                    ShotWithModAPI.attachments[context.argument(0)]?.let {
                        sender.inventory.setItemInMainHand(it.writeIntoGun(item))
                    }
                }
            }
        }
    }
}