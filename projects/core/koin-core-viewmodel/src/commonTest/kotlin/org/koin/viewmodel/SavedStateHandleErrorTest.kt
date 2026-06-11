package org.koin.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import org.koin.core.error.NoDefinitionFoundException
import org.koin.dsl.koinApplication
import org.koin.viewmodel.factory.AndroidParametersHolder
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Actionable error messages for the SavedStateHandle resolution failures behind
 * #2044 (intermittent "No definition found for SavedStateHandle") and
 * #2417 (CreationExtras must have SAVED_STATE_REGISTRY_OWNER_KEY).
 */
class SavedStateHandleErrorTest {

    // 1a — holder IS on the stack but extras lack a SavedStateRegistryOwner:
    // androidx.createSavedStateHandle() throws a raw IllegalArgumentException.
    // Koin should wrap it with an actionable message (#2417a).
    @Test
    fun savedStateHandle_without_owner_gives_actionable_koin_error_2417() {
        val holder = AndroidParametersHolder(null, CreationExtras.Empty)

        val error = assertFailsWith<Throwable> {
            holder.getOrNull<SavedStateHandle>(SavedStateHandle::class)
        }
        val msg = error.message ?: ""
        // distinctive Koin guidance — not the raw androidx message
        assertTrue(
            msg.contains("Koin could not create a SavedStateHandle"),
            "expected actionable Koin guidance, got: $msg",
        )
        assertTrue(msg.contains("SavedStateRegistryOwner"), "got: $msg")
    }

    // 1b — holder NOT on the stack: get<SavedStateHandle>() falls through to the
    // registry and throws NoDefinitionFound. The generic "add definition" tail is
    // wrong for SavedStateHandle (it comes from CreationExtras), so the message
    // must explain that instead (#2044).
    @Test
    fun savedStateHandle_no_holder_error_explains_creation_extras_2044() {
        val koin = koinApplication { }.koin

        val error = assertFailsWith<NoDefinitionFoundException> {
            koin.get<SavedStateHandle>()
        }
        val msg = error.message ?: ""
        assertTrue(msg.contains("SavedStateHandle"), "got: $msg")
        assertTrue(msg.contains("CreationExtras"), "expected extras guidance, got: $msg")
        assertFalse(
            msg.contains("add definition for type"),
            "must not suggest registering a SavedStateHandle definition, got: $msg",
        )
    }
}
