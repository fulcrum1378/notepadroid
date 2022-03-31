package com.farmerbb.notepad.ui.previews

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.farmerbb.notepad.R
import com.farmerbb.notepad.models.NoteMetadata
import com.farmerbb.notepad.ui.content.NoteListContent
import com.farmerbb.notepad.ui.widgets.AppBarText
import com.farmerbb.notepad.ui.widgets.MoreButton
import java.util.*

@Composable
private fun NoteList(notes: List<NoteMetadata>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { AppBarText(stringResource(id = R.string.app_name)) },
                backgroundColor = colorResource(id = R.color.primary),
                actions = {
                    MoreButton()
                }
            )
        },
        content = {
            NoteListContent(notes)
        }
    )
}

@Preview
@Composable
fun NoteListPreview() = MaterialTheme {
    NoteList(
        notes = listOf(
            NoteMetadata(
                metadataId = -1,
                title = "Test Note 1",
                date = Date()
            ),
            NoteMetadata(
                metadataId = -1,
                title = "Test Note 2",
                date = Date()
            )
        )
    )
}

@Preview
@Composable
fun NoteListEmptyPreview() = MaterialTheme {
    NoteList(notes = emptyList())
}
