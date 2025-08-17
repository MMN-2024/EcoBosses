package com.willfp.ecobosses.tick

import com.willfp.ecobosses.bosses.LivingEcoBoss

class EffectClearTicker : BossTicker {
    override fun tick(boss: LivingEcoBoss, tick: Int) {
        val entity = boss.entity
        val interval = boss.boss.effectClearInterval

        // If interval is -1 or 0, don't clear effects
        if (interval <= 0) {
            return
        }

        // Clear effects at the specified interval
        if (tick % interval == 0) {
            // Clear all active potion effects
            entity.activePotionEffects.forEach { effect ->
                entity.removePotionEffect(effect.type)
            }
        }
    }
}