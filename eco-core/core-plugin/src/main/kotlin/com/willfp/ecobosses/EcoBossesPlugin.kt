package com.willfp.ecobosses

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.ecobosses.bosses.Bosses
import com.willfp.ecobosses.bosses.EggDisplay
import com.willfp.ecobosses.bosses.bossHolders
import com.willfp.ecobosses.commands.CommandEcoBosses
import com.willfp.ecobosses.defence.DamageMultiplierHandler
import com.willfp.ecobosses.defence.ImmunitiesHandler
import com.willfp.ecobosses.defence.EffectImmunityHandler
import com.willfp.ecobosses.defence.MountHandler
import com.willfp.ecobosses.defence.PickupHandler
import com.willfp.ecobosses.integrations.levelledmobs.IntegrationLevelledMobs
import com.willfp.ecobosses.libreforge.EffectBossDropChanceMultiplier
import com.willfp.ecobosses.libreforge.MutatorLocationToBoss
import com.willfp.ecobosses.libreforge.TriggerKillBoss
import com.willfp.ecobosses.libreforge.TriggerSpawnBoss
import com.willfp.ecobosses.lifecycle.CompatibilityListeners
import com.willfp.ecobosses.lifecycle.ConsoleLoggers
import com.willfp.ecobosses.lifecycle.DeathListeners
import com.willfp.ecobosses.lifecycle.LifecycleHandlers
import com.willfp.ecobosses.spawn.AutospawnHandler
import com.willfp.ecobosses.spawn.SpawnEggHandler
import com.willfp.ecobosses.spawn.SpawnTotemHandler
import com.willfp.ecobosses.util.DiscoverRecipeListener
import com.willfp.ecobosses.util.TopDamagerListener
import com.willfp.eco.core.EcoPlugin
import org.bukkit.entity.Player
import org.bukkit.event.Listener

class EcoBossesPlugin : EcoPlugin() {
    init {
        instance = this
    }

    override fun handleAfterLoad() {
        // Register LibreForge integrations if available
        if (pluginManager.isPluginEnabled("libreforge")) {
            try {
                val effectsClass = Class.forName("com.willfp.libreforge.effects.Effects")
                val triggersClass = Class.forName("com.willfp.libreforge.triggers.Triggers")
                val mutatorsClass = Class.forName("com.willfp.libreforge.mutators.Mutators")
                
                val registerMethod = effectsClass.getMethod("register", Any::class.java)
                registerMethod.invoke(null, EffectBossDropChanceMultiplier)
                
                val registerTriggerMethod = triggersClass.getMethod("register", Any::class.java)
                registerTriggerMethod.invoke(null, TriggerKillBoss)
                registerTriggerMethod.invoke(null, TriggerSpawnBoss)
                
                val registerMutatorMethod = mutatorsClass.getMethod("register", Any::class.java)
                registerMutatorMethod.invoke(null, MutatorLocationToBoss)
                
                logger.info("LibreForge integration loaded successfully")
            } catch (e: Exception) {
                logger.warning("Failed to load LibreForge integration: ${e.message}")
            }
        }
    }

    override fun handleEnable() {
        // Register holder provider if LibreForge is available
        if (pluginManager.isPluginEnabled("libreforge")) {
            try {
                val holderProviderClass = Class.forName("com.willfp.libreforge.HolderProviderKt")
                val registerMethod = holderProviderClass.getMethod("registerSpecificHolderProvider", Class::class.java, kotlin.jvm.functions.Function1::class.java)
                registerMethod.invoke(null, Player::class.java) { player: Player ->
                    (player as Player).bossHolders
                }
            } catch (e: Exception) {
                logger.warning("Failed to register holder provider: ${e.message}")
            }
        }
    }

    override fun handleReload() {
        this.reload()
        Bosses.getAllAlive().forEach { it.remove() }
        AutospawnHandler.startSpawning(this)
    }

    override fun handleDisable() {
        Bosses.getAllAlive().forEach { it.remove() }
    }

    override fun loadConfigCategories(): List<Any> {
        return listOf(Bosses)
    }

    override fun createDisplayModule(): DisplayModule {
        return EggDisplay(this)
    }

    override fun loadPluginCommands(): List<PluginCommand> {
        return listOf(
            CommandEcoBosses(this)
        )
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            DiscoverRecipeListener(this),
            TopDamagerListener(),
            LifecycleHandlers(),
            SpawnEggHandler(this),
            DamageMultiplierHandler(),
            MountHandler(),
            PickupHandler(),
            ImmunitiesHandler(),
            EffectImmunityHandler(),
            CompatibilityListeners(),
            SpawnTotemHandler(),
            DeathListeners(),
            ConsoleLoggers(this)
        )
    }

    override fun loadIntegrationLoaders(): List<IntegrationLoader> {
        return listOf(
            IntegrationLoader("LevelledMobs") { this.eventManager.registerListener(IntegrationLevelledMobs()) }
        )
    }

    companion object {
        @JvmStatic
        lateinit var instance: EcoBossesPlugin
    }
}
