// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LevelGroupController(
    private val sectionIds: List<String>,
    initialCompleted: Set<String> = emptySet()
) {
    private val _completed = MutableStateFlow(initialCompleted)
    val completed: StateFlow<Set<String>> = _completed.asStateFlow()

    fun markCompleted(sectionId: String) {
        _completed.value = _completed.value + sectionId
    }

    fun isCompleted(sectionId: String): Boolean = sectionId in _completed.value

    val allCompleted: Boolean get() = sectionIds.all { it in _completed.value }

    val completedCount: Int get() = _completed.value.size

    val totalCount: Int get() = sectionIds.size

    fun syncFromPersisted(persisted: Set<String>) {
        _completed.value = _completed.value + persisted
    }

    fun reset() {
        _completed.value = emptySet()
    }
}
