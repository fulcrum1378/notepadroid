package com.farmerbb.notepad.ui.widgets

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.farmerbb.notepad.R

@Composable
fun NoteListMenu(
    showMenu: Boolean,
    onDismiss: () -> Unit,
    onMoreClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onImportClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    Box {
        MoreButton(onMoreClick)
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = onDismiss
        ) {
            MenuItem(R.string.action_settings, onSettingsClick)
            MenuItem(R.string.import_notes, onImportClick)
            MenuItem(R.string.dialog_about_title, onAboutClick)
        }
    }
}

@Composable
fun NoteViewEditMenu(
    showMenu: Boolean = false,
    onDismiss: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onExportClick: () -> Unit = {},
    onPrintClick: () -> Unit = {}
) {
    Box {
        MoreButton(onMoreClick)
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = onDismiss
        ) {
            MenuItem(R.string.action_share, onShareClick)
            MenuItem(R.string.action_export, onExportClick)
            MenuItem(R.string.action_print, onPrintClick)
        }
    }
}

@Composable
fun MenuItem(
    @StringRes stringRes: Int,
    onClick: () -> Unit
) {
    DropdownMenuItem(onClick = onClick) {
        Text(text = stringResource(id = stringRes))
    }
}
