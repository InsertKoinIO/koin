package org.koin.core.annotation

/**
* ## @Monitor - Koin Tracing and Performances Monitoring
*
* Marks a class or function for automatic monitoring and performance tracing through Kotzilla Platform,
* the official tooling platform for Koin.
*
* When applied to a class, generates a Koin proxy that monitors all public method calls.
* When applied to a function, monitors that specific method within a Koin-managed component.
*
* ## Basic Usage:
* ```kotlin
* @Monitor
* class UserService(private val userRepository: UserRepository) {
    *     fun findUser(id: String): User? = userRepository.findById(id)
    * }
* ```
*
* ## Requirements:
* - `implementation 'io.kotzilla:kotzilla-core:latest.version'`
* - Valid Kotzilla Platform account and API key (see setup at Koin startup)
*
* The proxy automatically captures performance metrics, error rates, and usage patterns,
* sending data to your Kotzilla workspace for real-time analysis.
*
* @see <a href="https://doc.kotzilla.io">Complete Documentation</a>
* @see <a href="https://doc.kotzilla.io/docs/releaseNotes/changelogSDK">Latest Version</a>
* @see <a href="https://kotzilla.io">Kotzilla Platform</a>
*
* @since Kotzilla 1.2.1
*/
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
public annotation class Monitor