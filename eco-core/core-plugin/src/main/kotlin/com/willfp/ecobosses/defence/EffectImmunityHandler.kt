package com.willfp.ecobosses.defence

import com.willfp.ecobosses.bosses.Bosses
import org.bukkit.entity.AreaEffectCloud
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.ThrownPotion
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.AreaEffectCloudApplyEvent
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.entity.PotionSplashEvent

class EffectImmunityHandler : Listener {
    @EventHandler(
        ignoreCancelled = true
    )
    fun handlePotionEffect(event: EntityPotionEffectEvent) {
        val entity = event.entity as? LivingEntity ?: return
        val boss = Bosses[entity]?.boss ?: return

        if (!boss.isImmuneToEffects) {
            return
        }

        // Cancel all potion effects except those applied by the plugin itself
        when (event.cause) {
            EntityPotionEffectEvent.Cause.POTION_DRINK,
            EntityPotionEffectEvent.Cause.POTION_SPLASH,
            EntityPotionEffectEvent.Cause.AREA_EFFECT_CLOUD,
            EntityPotionEffectEvent.Cause.ARROW,
            EntityPotionEffectEvent.Cause.BEACON,
            EntityPotionEffectEvent.Cause.CONDUIT,
            EntityPotionEffectEvent.Cause.FOOD,
            EntityPotionEffectEvent.Cause.MILK,
            EntityPotionEffectEvent.Cause.TURTLE_HELMET,
            EntityPotionEffectEvent.Cause.VILLAGER_TRADE,
            EntityPotionEffectEvent.Cause.WARDEN -> {
                event.isCancelled = true
            }
            else -> {
                // Allow effects from other sources like plugins, commands, etc.
            }
        }
    }

    @EventHandler(
        ignoreCancelled = true
    )
    fun handleSplashPotion(event: PotionSplashEvent) {
        val affectedEntities = event.affectedEntities.toList()
        
        for (entity in affectedEntities) {
            val boss = Bosses[entity]?.boss ?: continue
            
            if (boss.isImmuneToEffects) {
                event.setIntensity(entity, 0.0)
            }
        }
    }

    @EventHandler(
        ignoreCancelled = true
    )
    fun handleAreaEffectCloud(event: AreaEffectCloudApplyEvent) {
        val affectedEntities = event.affectedEntities.toList()
        
        for (entity in affectedEntities) {
            val boss = Bosses[entity]?.boss ?: continue
            
            if (boss.isImmuneToEffects) {
                event.affectedEntities.remove(entity)
            }
        }
    }
}