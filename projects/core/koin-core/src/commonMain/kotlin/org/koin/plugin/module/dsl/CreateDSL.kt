package org.koin.plugin.module.dsl

import org.koin.core.module.KoinDslMarker
import org.koin.core.scope.Scope

public const val USE_KOIN_COMPILER_PLUGIN : String = "The Koin Compiler plugin is missing. Please fix your setup to use 'io.insert-koin.compiler.plugin' Koin Gradle Plugin."
public fun USE_KOIN_COMPILER_PLUGIN(msg : String): Nothing = throw NotImplementedError("Error while using '$msg'. $USE_KOIN_COMPILER_PLUGIN")

// Scope.create(::T) stubs - With Koin
@KoinDslMarker
public inline fun <reified T> Scope.create(function: () -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?> Scope.create(function: (r1 : R1) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?> Scope.create(function: (r1 : R1, r2 : R2) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?, R5:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4, r5 : R5) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?, R5:Any?, R6:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4, r5 : R5, r6 : R6) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?, R5:Any?, R6:Any?, R7:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4, r5 : R5, r6 : R6, r7 : R7) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?, R5:Any?, R6:Any?, R7:Any?, R8:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4, r5 : R5, r6 : R6, r7 : R7, r8 : R8) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?, R5:Any?, R6:Any?, R7:Any?, R8:Any?, R9:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4, r5 : R5, r6 : R6, r7 : R7, r8 : R8, r9 : R9) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?, R5:Any?, R6:Any?, R7:Any?, R8:Any?, R9:Any?, R10:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4, r5 : R5, r6 : R6, r7 : R7, r8 : R8, r9 : R9, r10 : R10) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?, R5:Any?, R6:Any?, R7:Any?, R8:Any?, R9:Any?, R10:Any?, R11:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4, r5 : R5, r6 : R6, r7 : R7, r8 : R8, r9 : R9, r10 : R10, r11 : R11) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?, R5:Any?, R6:Any?, R7:Any?, R8:Any?, R9:Any?, R10:Any?, R11:Any?, R12:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4, r5 : R5, r6 : R6, r7 : R7, r8 : R8, r9 : R9, r10 : R10, r11 : R11, r12 : R12) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?, R5:Any?, R6:Any?, R7:Any?, R8:Any?, R9:Any?, R10:Any?, R11:Any?, R12:Any?, R13:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4, r5 : R5, r6 : R6, r7 : R7, r8 : R8, r9 : R9, r10 : R10, r11 : R11, r12 : R12, r13 : R13) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?, R5:Any?, R6:Any?, R7:Any?, R8:Any?, R9:Any?, R10:Any?, R11:Any?, R12:Any?, R13:Any?, R14:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4, r5 : R5, r6 : R6, r7 : R7, r8 : R8, r9 : R9, r10 : R10, r11 : R11, r12 : R12, r13 : R13, r14 : R14) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?, R5:Any?, R6:Any?, R7:Any?, R8:Any?, R9:Any?, R10:Any?, R11:Any?, R12:Any?, R13:Any?, R14:Any?, R15:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4, r5 : R5, r6 : R6, r7 : R7, r8 : R8, r9 : R9, r10 : R10, r11 : R11, r12 : R12, r13 : R13, r14 : R14, r15 : R15) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?, R5:Any?, R6:Any?, R7:Any?, R8:Any?, R9:Any?, R10:Any?, R11:Any?, R12:Any?, R13:Any?, R14:Any?, R15:Any?, R16:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4, r5 : R5, r6 : R6, r7 : R7, r8 : R8, r9 : R9, r10 : R10, r11 : R11, r12 : R12, r13 : R13, r14 : R14, r15 : R15, r16 : R16) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?, R5:Any?, R6:Any?, R7:Any?, R8:Any?, R9:Any?, R10:Any?, R11:Any?, R12:Any?, R13:Any?, R14:Any?, R15:Any?, R16:Any?, R17:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4, r5 : R5, r6 : R6, r7 : R7, r8 : R8, r9 : R9, r10 : R10, r11 : R11, r12 : R12, r13 : R13, r14 : R14, r15 : R15, r16 : R16, r17 : R17) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?, R5:Any?, R6:Any?, R7:Any?, R8:Any?, R9:Any?, R10:Any?, R11:Any?, R12:Any?, R13:Any?, R14:Any?, R15:Any?, R16:Any?, R17:Any?, R18:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4, r5 : R5, r6 : R6, r7 : R7, r8 : R8, r9 : R9, r10 : R10, r11 : R11, r12 : R12, r13 : R13, r14 : R14, r15 : R15, r16 : R16, r17 : R17, r18 : R18) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?, R5:Any?, R6:Any?, R7:Any?, R8:Any?, R9:Any?, R10:Any?, R11:Any?, R12:Any?, R13:Any?, R14:Any?, R15:Any?, R16:Any?, R17:Any?, R18:Any?, R19:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4, r5 : R5, r6 : R6, r7 : R7, r8 : R8, r9 : R9, r10 : R10, r11 : R11, r12 : R12, r13 : R13, r14 : R14, r15 : R15, r16 : R16, r17 : R17, r18 : R18, r19 : R19) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?, R5:Any?, R6:Any?, R7:Any?, R8:Any?, R9:Any?, R10:Any?, R11:Any?, R12:Any?, R13:Any?, R14:Any?, R15:Any?, R16:Any?, R17:Any?, R18:Any?, R19:Any?, R20:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4, r5 : R5, r6 : R6, r7 : R7, r8 : R8, r9 : R9, r10 : R10, r11 : R11, r12 : R12, r13 : R13, r14 : R14, r15 : R15, r16 : R16, r17 : R17, r18 : R18, r19 : R19, r20 : R20) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
@KoinDslMarker
public inline fun <reified T, R1:Any?, R2:Any?, R3:Any?, R4:Any?, R5:Any?, R6:Any?, R7:Any?, R8:Any?, R9:Any?, R10:Any?, R11:Any?, R12:Any?, R13:Any?, R14:Any?, R15:Any?, R16:Any?, R17:Any?, R18:Any?, R19:Any?, R20:Any?, R21:Any?> Scope.create(function: (r1 : R1, r2 : R2, r3 : R3, r4 : R4, r5 : R5, r6 : R6, r7 : R7, r8 : R8, r9 : R9, r10 : R10, r11 : R11, r12 : R12, r13 : R13, r14 : R14, r15 : R15, r16 : R16, r17 : R17, r18 : R18, r19 : R19, r20 : R20, r21 : R21) -> T): T { USE_KOIN_COMPILER_PLUGIN("Scope.create(::T)") }
