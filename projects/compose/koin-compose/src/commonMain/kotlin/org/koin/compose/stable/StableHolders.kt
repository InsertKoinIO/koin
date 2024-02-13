package org.koin.compose.stable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import org.koin.core.parameter.ParametersDefinition

@Stable
class StableParametersDefinition(val parametersDefinition: ParametersDefinition?)

@Composable
fun rememberStableParametersDefinition(
    parametersDefinition: ParametersDefinition?
): StableParametersDefinition = remember { StableParametersDefinition(parametersDefinition) }