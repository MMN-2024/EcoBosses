package com.willfp.ecobosses.bosses

import com.google.common.collect.ImmutableList
import com.willfp.eco.core.registry.Registry
import com.willfp.eco.core.config.ConfigType
import com.willfp.ecobosses.EcoBossesPlugin
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import java.io.File
import java.util.UUID

object Bosses {
    /** Registered bosses. */
    private val registry = Registry<EcoBoss>()

    fun reload(plugin: EcoBossesPlugin) {
        registry.clear()
        
        val bossesDir = File(plugin.dataFolder, "bosses")
        if (!bossesDir.exists()) {
            bossesDir.mkdirs()
        }
        
        loadBossesFromDirectory(bossesDir, plugin)
    }
    
    private fun loadBossesFromDirectory(directory: File, plugin: EcoBossesPlugin) {
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                loadBossesFromDirectory(file, plugin)
            } else if (file.extension == "yml" && !file.name.startsWith("_")) {
                try {
                    val config = plugin.createConfig(file.nameWithoutExtension, true, ConfigType.YAML)
                    val id = file.nameWithoutExtension
                    registry.register(EcoBoss(id, config, plugin))
                } catch (e: Exception) {
                    plugin.logger.severe("Failed to load boss from ${file.name}: ${e.message}")
                }
            }
        }
    }

    /**
     * Get all registered [EcoBoss]s.
     *
     * @return A list of all [EcoBoss]s.
     */
    @JvmStatic
    fun values(): List<EcoBoss> {
        return ImmutableList.copyOf(registry.values())
    }

    /**
     * Get [EcoBoss] matching ID.
     *
     * @param name The name to search for.
     * @return The matching [EcoBoss], or null if not found.
     */
    @JvmStatic
    fun getByID(name: String): EcoBoss? {
        return registry[name]
    }

    /**
     * Get all currently alive [EcoBoss]es.
     *
     * @return All living bosses.
     */
    @JvmStatic
    fun getAllAlive(): Set<LivingEcoBoss> {
        val entities = mutableSetOf<LivingEcoBoss>()

        for (boss in values()) {
            entities.addAll(boss.getAllAlive())
        }

        return entities
    }

    /**
     * Get [LivingEcoBoss].
     *
     * @return The boss, or null if not a boss.
     */
    operator fun get(uuid: UUID): LivingEcoBoss? {
        for (boss in values()) {
            val found = boss[uuid]

            if (found != null) {
                return found
            }
        }

        return null
    }

    /**
     * Get [LivingEcoBoss].
     *
     * @return The boss, or null if not a boss.
     */
    operator fun get(entity: LivingEntity): LivingEcoBoss? {
        return get(entity.uniqueId)
    }

    /** If an entity is a boss. */
    val Entity?.isBoss: Boolean
        get() {
            if (this !is LivingEntity) {
                return false
            }

            return Bosses[this] != null
        }
}