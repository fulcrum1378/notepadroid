package com.farmerbb.notepad.data

import com.farmerbb.notepad.Database
import com.farmerbb.notepad.models.*
import com.farmerbb.notepad.models.SortOrder.*
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import java.util.*

class NotepadRepository(private val database: Database) {
    fun noteMetadataFlow(order: SortOrder) = with(database.noteMetadataQueries) {
        when (order) {
            DateDescending -> getSortedByDateDescending()
            DateAscending -> getSortedByDateAscending()
            TitleDescending -> getSortedByTitleDescending()
            TitleAscending -> getSortedByTitleAscending()
        }
    }.asFlow().mapToList()

    fun getNote(id: Long): Note = with(database) {
        transactionWithResult {
            val metadata = noteMetadataQueries.get(id).executeAsOne()
            val crossRef = crossRefQueries.get(metadata.metadataId).executeAsOne()
            val contents = noteContentsQueries.get(crossRef.contentsId).executeAsOne()

            Note(metadata = metadata, contents = contents)
        }
    }

    fun saveNote(id: Long, text: String, onSuccess: (Long) -> Unit) = try {
        val crossRef = database.crossRefQueries.get(id).executeAsOneOrNull()

        val metadata = NoteMetadata(
            metadataId = crossRef?.metadataId ?: -1,
            title = text.substringBefore("\n"),
            date = Date()
        )

        val contents = NoteContents(
            contentsId = crossRef?.contentsId ?: -1,
            text = text,
            isDraft = false
        )

        with(database) {
            crossRef?.let {
                noteMetadataQueries.update(metadata)
                noteContentsQueries.update(contents)
                onSuccess(id)
            } ?: run {
                noteMetadataQueries.insert(metadata)
                noteContentsQueries.insert(contents)

                val newCrossRef = CrossRef(
                    metadataId = noteMetadataQueries.getIndex().executeAsOne(),
                    contentsId = noteContentsQueries.getIndex().executeAsOne()
                )

                crossRefQueries.insert(newCrossRef)
                onSuccess(newCrossRef.metadataId)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    fun deleteNote(id: Long, onSuccess: () -> Unit) = try {
        with(database) {
            crossRefQueries.get(id).executeAsOneOrNull()?.let {
                noteMetadataQueries.delete(it.metadataId)
                noteContentsQueries.delete(it.contentsId)
                crossRefQueries.delete(id)
            }
        }
        onSuccess()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
