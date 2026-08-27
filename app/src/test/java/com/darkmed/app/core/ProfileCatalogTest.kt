package com.darkmed.app.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ProfileCatalogTest {
    @Test
    fun createRenameDuplicateAndDeleteArePersistentModelOperations() {
        val initial = listOf(ConnectionProfile("one", "One"))
        val created = ProfileCatalog.create(initial, "Two", "two")
        val renamed = ProfileCatalog.rename(created, "two", "Renamed")
        val duplicated = ProfileCatalog.duplicate(renamed, "one", "One Copy")
        val deleted = ProfileCatalog.delete(duplicated, "two")
        assertEquals(listOf("One", "One Copy"), deleted.map { it.name })
    }

    @Test
    fun blankAndDuplicateNamesAreRejected() {
        val initial = listOf(ConnectionProfile("one", "One"))
        assertThrows(IllegalArgumentException::class.java) { ProfileCatalog.create(initial, "   ", "two") }
        assertThrows(IllegalArgumentException::class.java) { ProfileCatalog.create(initial, "one", "two") }
        assertThrows(IllegalArgumentException::class.java) { ProfileCatalog.rename(initial, "one", "   ") }
    }

    @Test
    fun unknownProfileOperationsAreRejected() {
        val initial = listOf(ConnectionProfile("one", "One"))
        assertThrows(IllegalArgumentException::class.java) { ProfileCatalog.rename(initial, "missing", "Two") }
        assertThrows(IllegalArgumentException::class.java) { ProfileCatalog.duplicate(initial, "missing", "Two") }
        assertThrows(IllegalArgumentException::class.java) { ProfileCatalog.delete(initial, "missing") }
    }
}
