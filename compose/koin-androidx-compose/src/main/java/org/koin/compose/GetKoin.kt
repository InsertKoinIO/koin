package org.koin.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.core.Koin
import org.koin.core.context.GlobalContext

@Composable
fun getKoin(): Koin = remember {
    GlobalContext.get()
}
