package org.koin.module

/**
 * Empty Module - used when no module is used with Koin() builder
 * @author - Arnaud GIULIANI
 */
class EmptyModule : Module() {
    override fun onLoad() {
        declareContext { }
    }
}