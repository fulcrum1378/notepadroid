@file:OptIn(FlowPreview::class)

package com.farmerbb.notepad.android

import android.app.Application
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farmerbb.notepad.R
import com.farmerbb.notepad.data.NotepadRepository
import com.farmerbb.notepad.data.PreferenceManager.Companion.prefs
import com.farmerbb.notepad.models.Note
import com.farmerbb.notepad.utils.showToast
import de.schnettler.datastore.manager.DataStoreManager
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch

class NotepadViewModel(
    private val context: Application,
    private val repo: NotepadRepository,
    dataStoreManager: DataStoreManager
) : ViewModel() {
    private val _noteState = MutableStateFlow(Note())
    val noteState: StateFlow<Note> = _noteState
    val noteMetadata get() = prefs.sortOrder.flatMapConcat(repo::noteMetadataFlow)
    val prefs = dataStoreManager.prefs(viewModelScope)

    fun getNote(id: Long?) = id?.let {
        _noteState.value = repo.getNote(it)
    } ?: run {
        clearNote()
    }

    fun clearNote() {
        _noteState.value = Note()
    }

    fun saveNote(
        id: Long,
        text: String,
        onSuccess: (Long) -> Unit
    ) = viewModelScope.launch {
        text.checkLength {
            repo.saveNote(id, text, onSuccess)
        }
    }

    fun deleteNote(
        id: Long,
        onSuccess: () -> Unit
    ) = viewModelScope.launch {
        repo.deleteNote(id, onSuccess)
    }

    fun shareNote(text: String) = viewModelScope.launch {
        text.checkLength {
            showShareSheet(text)
        }
    }

    private fun showShareSheet(text: String) = with(context) {
        try {
            startActivity(
                Intent.createChooser(
                    Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    },
                    getString(R.string.send_to)
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun String.checkLength(onSuccess: suspend () -> Unit) = when (length) {
        0 -> context.showToast(R.string.empty_note)
        else -> onSuccess()
    }

    // TODO implement the following functions:
    fun importNotes() {}
    fun exportNote(text: String) {}
    fun printNote(text: String) {}
}
