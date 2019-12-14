package org.koin.ktor.ext

import io.ktor.application.EventDefinition
import org.koin.core.KoinApplication

/**
 * @author Victor Alenkov
 */

/**
 * Event definition for [KoinApplication] Started event
 */
val KoinApplicationStarted = EventDefinition<KoinApplication>()

/**
 * Event definition for an event that is fired when the [KoinApplication] is going to stop
 */
val KoinApplicationStopPreparing = EventDefinition<KoinApplication>()

/**
 * Event definition for [KoinApplication] Stopping event
 */
val KoinApplicationStopped = EventDefinition<KoinApplication>()
