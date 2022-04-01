package ir.mahdiparastesh.notepad.models

import java.util.*

data class Note(
    val metadata: NoteMetadata = NoteMetadata(
        metadataId = -1,
        title = "",
        date = Date()
    ),
    val contents: NoteContents = NoteContents(
        contentsId = -1,
        text = "",
        isDraft = false
    )
)
