package com.willfp.ecobosses.bosses

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.display.DisplayPriority
import com.willfp.eco.core.fast.fast
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class EggDisplay(
    plugin: EcoPlugin
) : DisplayModule(
    plugin,
    DisplayPriority.LOW
) {
    override fun display(itemStack: ItemStack, player: Player?, vararg args: Any) {
        if (player == null) {
            return
        }

        val fis = itemStack.fast()

        val lore = fis.lore.toMutableList()

        val egg = itemStack.bossEgg ?: return

        val lines = try {
            if (plugin.pluginManager.isPluginEnabled("libreforge")) {
                // Use reflection to check spawn conditions if LibreForge is available
                val toDispatcherMethod = Class.forName("com.willfp.libreforge.DispatcherKt")
                    .getMethod("toDispatcher", Any::class.java)
                val dispatcher = toDispatcherMethod.invoke(null, player)
                // For now, just return empty list - full implementation would require more reflection
                emptyList<String>()
            } else {
                emptyList<String>()
            }
        } catch (e: Exception) {
            emptyList<String>()
        }
        
        if (lines.isNotEmpty()) {
            lore.add(Display.PREFIX)
            lore.addAll(lines)
        }

        fis.lore = lore
    }
}
