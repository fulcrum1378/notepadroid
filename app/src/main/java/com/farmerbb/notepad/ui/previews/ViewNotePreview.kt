package com.farmerbb.notepad.ui.previews

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.farmerbb.notepad.R
import com.farmerbb.notepad.models.Note
import com.farmerbb.notepad.models.NoteContents
import com.farmerbb.notepad.models.NoteMetadata
import com.farmerbb.notepad.ui.content.ViewNoteContent
import com.farmerbb.notepad.ui.widgets.*
import java.util.*

@Composable
private fun ViewNote(note: Note) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton() },
                title = { AppBarText(note.metadata.title) },
                backgroundColor = colorResource(id = R.color.primary),
                actions = {
                    EditButton()
                    DeleteButton()
                    NoteViewEditMenu()
                }
            )
        },
        content = { ViewNoteContent(note.contents.text) }
    )
}

@Preview
@Composable
fun ViewNotePreview() = MaterialTheme {
    ViewNote(
        note = Note(
            metadata = NoteMetadata(
                metadataId = -1,
                title = "Title",
                date = Date()
            ),
            contents = NoteContents(
                contentsId = -1,
                text = "This is some text",
                isDraft = false
            )
        ),
    )
}
