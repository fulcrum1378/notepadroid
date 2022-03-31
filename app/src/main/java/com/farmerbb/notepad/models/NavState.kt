package com.farmerbb.notepad.models

sealed interface NavState {
    object Empty : NavState
    data class View(val id: Long) : NavState
    data class Edit(val id: Long? = null) : NavState

    companion object {
        const val VIEW = "View"
        const val EDIT = "Edit"
    }
}
