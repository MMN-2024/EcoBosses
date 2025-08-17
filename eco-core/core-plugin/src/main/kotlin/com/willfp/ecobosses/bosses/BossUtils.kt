package com.willfp.ecobosses.bosses

import com.willfp.eco.core.fast.fast
import com.willfp.ecobosses.EcoBossesPlugin
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.math.pow

private val spawnEggKey = EcoBossesPlugin.instance.namespacedKeyFactory.create("spawn_egg")

var ItemStack.bossEgg: EcoBoss?
    set(value) {
        val meta = this.itemMeta ?: return
        val pdc = meta.persistentDataContainer
        if (value == null) {
            pdc.remove(spawnEggKey)
        } else {
            pdc.set(spawnEggKey, PersistentDataType.STRING, value.id.key)
        }
        this.itemMeta = meta
    }
    get() {
        val pdc = this.fast().persistentDataContainer
        val id = pdc.get(spawnEggKey, PersistentDataType.STRING) ?: return null
        return Bosses.getByID(id)
    }
