package org.koin.viewmodel

import androidx.lifecycle.viewmodel.CreationExtras
import org.koin.core.parameter.parameterArrayOf
import org.koin.core.parameter.parameterSetOf
import org.koin.core.parameter.parametersOf
import org.koin.viewmodel.factory.AndroidParametersHolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * [AndroidParametersHolder] wraps the parameters given to a ViewModel, so it must keep how they are consumed
 * (`parameterArrayOf` / `parameterSetOf`) and not only their values.
 *
 * @author - Alex Lillo
 */
class AndroidParametersHolderModeTest {

    @Test
    fun keepsIndexedMode() {
        val holder = AndroidParametersHolder({ parameterArrayOf("a", null, "c") }, CreationExtras.Empty)

        assertEquals(true, holder.useIndexedValues)
        assertEquals(listOf("a", null, "c"), holder.values)
    }

    @Test
    fun keepsSetMode() {
        val holder = AndroidParametersHolder({ parameterSetOf("a", 42) }, CreationExtras.Empty)

        assertEquals(false, holder.useIndexedValues)
    }

    @Test
    fun keepsDefaultMode() {
        val holder = AndroidParametersHolder({ parametersOf("a") }, CreationExtras.Empty)

        assertNull(holder.useIndexedValues)
    }

    @Test
    fun emptyWithoutParameters() {
        val holder = AndroidParametersHolder(null, CreationExtras.Empty)

        assertNull(holder.useIndexedValues)
        assertEquals(emptyList(), holder.values)
    }
}
