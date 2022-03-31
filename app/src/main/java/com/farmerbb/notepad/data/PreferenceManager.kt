package com.farmerbb.notepad.data

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.farmerbb.notepad.R
import com.farmerbb.notepad.models.FilenameFormat
import com.farmerbb.notepad.models.Prefs
import com.farmerbb.notepad.models.SortOrder
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.datastore.manager.PreferenceRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PreferenceManager private constructor(
    private val dataStoreManager: DataStoreManager,
    private val scope: CoroutineScope
) {
    val isLightTheme get() = Prefs.Theme.mapToFlow { theme -> theme.contains("light") }

    val backgroundColorRes = R.color.window_background

    val primaryColorRes = R.color.text_color_primary

    val secondaryColorRes = R.color.text_color_secondary

    val textFontSize
        get() = Prefs.FontSize.mapToFlow { fontSize ->
            when (fontSize) {
                "smallest" -> 12.sp
                "small" -> 14.sp
                "normal" -> 16.sp
                "large" -> 18.sp
                else -> 20.sp
            }
        }

    val dateFontSize
        get() = Prefs.FontSize.mapToFlow { fontSize ->
            when (fontSize) {
                "smallest" -> 8.sp
                "small" -> 10.sp
                "normal" -> 12.sp
                "large" -> 14.sp
                else -> 16.sp
            }
        }

    val fontFamily
        get() = Prefs.Theme.mapToFlow { theme ->
            when {
                theme.contains("sans") -> FontFamily.SansSerif
                theme.contains("serif") -> FontFamily.Serif
                else -> FontFamily.Monospace
            }
        }

    val markdownFont
        get() = Prefs.Theme.mapToFlow { theme ->
            when {
                theme.contains("sans") -> "sans"
                theme.contains("serif") -> "serif"
                else -> "monospace"
            }
        }

    val sortOrder get() = Prefs.SortBy.mapToFlow(::toSortOrder)
    val filenameFormat get() = Prefs.ExportFilename.mapToFlow(::toFilenameFormat)
    val showDialogs get() = Prefs.ShowDialogs.asFlow
    val showDate get() = Prefs.ShowDate.asFlow
    val directEdit get() = Prefs.DirectEdit.asFlow
    val markdown get() = Prefs.Markdown.asFlow

    private fun <T, R> PreferenceRequest<T>.mapToFlow(transform: (value: T) -> R) =
        dataStoreManager.getPreferenceFlow(this).map(transform).stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = transform(defaultValue)
        )

    private val <T> PreferenceRequest<T>.asFlow get() = mapToFlow { it }

    private fun toSortOrder(value: String) = SortOrder.values().first {
        it.stringValue == value
    }

    private fun toFilenameFormat(value: String) = FilenameFormat.values().first {
        it.stringValue == value
    }

    companion object {
        fun DataStoreManager.prefs(scope: CoroutineScope) =
            PreferenceManager(dataStoreManager = this, scope = scope)
    }
}
