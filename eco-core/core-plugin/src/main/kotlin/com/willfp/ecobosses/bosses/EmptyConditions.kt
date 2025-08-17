package com.willfp.ecobosses.bosses

/**
 * Empty conditions implementation for when LibreForge is not available
 */
class EmptyConditions {
    fun areMet(dispatcher: Any, holder: Any): Boolean = true
    fun filterNot(predicate: (Any) -> Boolean): List<Any> = emptyList()
}

/**
 * Empty effects implementation for when LibreForge is not available
 */
class EmptyEffects