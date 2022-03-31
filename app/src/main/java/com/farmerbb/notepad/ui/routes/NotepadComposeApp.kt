package com.farmerbb.notepad.ui.routes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.farmerbb.notepad.R
import com.farmerbb.notepad.android.NotepadViewModel
import com.farmerbb.notepad.models.NavState
import com.farmerbb.notepad.models.NavState.*
import com.farmerbb.notepad.models.NavState.Companion.EDIT
import com.farmerbb.notepad.models.NavState.Companion.VIEW
import com.farmerbb.notepad.ui.content.EditNoteContent
import com.farmerbb.notepad.ui.content.NoteListContent
import com.farmerbb.notepad.ui.content.ViewNoteContent
import com.farmerbb.notepad.ui.widgets.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.androidx.compose.getViewModel

@Composable
fun NotepadComposeApp() {
    val vm: NotepadViewModel = getViewModel()
    val systemUiController = rememberSystemUiController()
    val configuration = LocalConfiguration.current
    val isLightTheme by vm.prefs.isLightTheme.collectAsState()

    MaterialTheme {
        NotepadComposeApp(
            vm = vm,
            isMultiPane = configuration.screenWidthDp >= 600
        )
    }

    LaunchedEffect(isLightTheme) {
        systemUiController.setNavigationBarColor(
            color = if (isLightTheme) Color.White else Color.Black
        )
    }
}

@Composable
fun NotepadComposeApp(
    vm: NotepadViewModel = getViewModel(),
    isMultiPane: Boolean = false,
    initState: NavState = Empty
) {
    val notes by vm.noteMetadata.collectAsState(emptyList())
    val note by vm.noteState.collectAsState()
    var navState by rememberSaveable(
        saver = Saver(
            save = {
                when (val state = it.value) {
                    is View -> VIEW to state.id
                    is Edit -> EDIT to state.id
                    else -> "" to null
                }
            },
            restore = {
                mutableStateOf(
                    when (it.first) {
                        VIEW -> View(it.second ?: 0)
                        EDIT -> Edit(it.second)
                        else -> Empty
                    }
                )
            }
        )
    ) { mutableStateOf(initState) }

    var showAboutDialog by remember { mutableStateOf(false) }
    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false },
            checkForUpdates = { showAboutDialog = false }
        )
    }

    var showSettingsDialog by remember { mutableStateOf(false) }
    if (showSettingsDialog) {
        SettingsDialog(
            onDismiss = {
                showSettingsDialog = false
            }
        )
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var noteToDelete: Long? by remember { mutableStateOf(null) }
    if (showDeleteDialog) {
        DeleteAlertDialog(
            onConfirm = {
                showDeleteDialog = false
                vm.deleteNote(id = noteToDelete ?: -1L) {
                    navState = Empty
                }
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }

    val title: String
    val backButton: @Composable (() -> Unit)?
    val actions: @Composable RowScope.() -> Unit
    val content: @Composable BoxScope.() -> Unit

    var showMenu by remember { mutableStateOf(false) }
    val onDismiss = { showMenu = false }
    val onMoreClick = { showMenu = true }
    val onDeleteClick: (Long?) -> Unit = {
        noteToDelete = it
        showDeleteDialog = true
    }
    val onShareClick: (String) -> Unit = {
        onDismiss()
        vm.shareNote(it)
    }
    val onExportClick: (String) -> Unit = {
        onDismiss()
        vm.exportNote(it)
    }
    val onPrintClick: (String) -> Unit = {
        onDismiss()
        vm.printNote(it)
    }
    val onBack = { navState = Empty }

    BackHandler(
        enabled = navState != Empty,
        onBack = onBack
    )

    val backgroundColorRes = vm.prefs.backgroundColorRes
    val primaryColorRes = vm.prefs.primaryColorRes
    val secondaryColorRes = vm.prefs.secondaryColorRes
    val textFontSize by vm.prefs.textFontSize.collectAsState()
    val dateFontSize by vm.prefs.dateFontSize.collectAsState()
    val fontFamily by vm.prefs.fontFamily.collectAsState()
    val showDate by vm.prefs.showDate.collectAsState()
    val markdown by vm.prefs.markdown.collectAsState()

    val textStyle = TextStyle(
        color = colorResource(id = primaryColorRes),
        fontSize = textFontSize,
        fontFamily = fontFamily
    )
    val dateStyle = TextStyle(
        color = colorResource(id = secondaryColorRes),
        fontSize = dateFontSize,
        fontFamily = fontFamily
    )

    @Composable
    fun NoteListContentShared() = NoteListContent(
        notes = notes,
        textStyle = textStyle,
        dateStyle = dateStyle,
        showDate = showDate
    ) { id ->
        navState = View(id)
    }

    when (val state = navState) {
        Empty -> {
            LaunchedEffect(Unit) {
                vm.clearNote()
            }

            title = stringResource(id = R.string.app_name)
            backButton = null
            actions = {
                NoteListMenu(
                    showMenu = showMenu,
                    onDismiss = onDismiss,
                    onMoreClick = onMoreClick,
                    onSettingsClick = {
                        onDismiss()
                        showSettingsDialog = true
                    },
                    onImportClick = {
                        onDismiss()
                        vm.importNotes()
                    },
                    onAboutClick = {
                        onDismiss()
                        showAboutDialog = true
                    }
                )
            }
            content = {
                if (isMultiPane) {
                    EmptyDetails()
                } else {
                    NoteListContentShared()
                }
            }
        }

        is View -> {
            LaunchedEffect(state.id) {
                vm.getNote(state.id)
            }

            title = note.metadata.title
            backButton = { BackButton(onBack) }
            actions = {
                EditButton { navState = Edit(state.id) }
                DeleteButton { onDeleteClick(state.id) }
                NoteViewEditMenu(
                    showMenu = showMenu,
                    onDismiss = onDismiss,
                    onMoreClick = onMoreClick,
                    onShareClick = { onShareClick(note.contents.text) },
                    onExportClick = { onExportClick(note.contents.text) },
                    onPrintClick = { onPrintClick(note.contents.text) }
                )
            }
            content = {
                ViewNoteContent(
                    text = note.contents.text,
                    textStyle = textStyle,
                    markdown = markdown
                )
            }
        }

        is Edit -> {
            LaunchedEffect(state.id) {
                vm.getNote(state.id)
            }

            var text by rememberSaveable { mutableStateOf(note.contents.text) }
            val id = note.metadata.metadataId

            title = note.metadata.title.ifEmpty {
                stringResource(id = R.string.action_new)
            }
            backButton = { BackButton(onBack) }
            actions = {
                SaveButton {
                    vm.saveNote(id, text) { newId ->
                        navState = View(newId)
                    }
                }
                DeleteButton { onDeleteClick(id) }
                NoteViewEditMenu(
                    showMenu = showMenu,
                    onDismiss = onDismiss,
                    onMoreClick = onMoreClick,
                    onShareClick = { onShareClick(text) },
                    onExportClick = { onExportClick(text) },
                    onPrintClick = { onPrintClick(text) }
                )
            }
            content = {
                EditNoteContent(
                    text = text,
                    textStyle = textStyle
                ) { text = it }
            }
        }
    }

    Scaffold(
        backgroundColor = colorResource(id = backgroundColorRes),
        topBar = {
            TopAppBar(
                navigationIcon = backButton,
                title = { AppBarText(title) },
                backgroundColor = colorResource(id = R.color.primary),
                actions = actions
            )
        },
        floatingActionButton = {
            if (navState == Empty) {
                FloatingActionButton(
                    onClick = { navState = Edit() },
                    backgroundColor = colorResource(id = R.color.primary),
                    content = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                )
            }
        },
        content = {
            if (isMultiPane) {
                Row {
                    Box(modifier = Modifier.weight(1f)) {
                        NoteListContentShared()
                    }

                    Divider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                    )

                    Box(
                        modifier = Modifier.weight(2f),
                        content = content
                    )
                }
            } else {
                Box(content = content)
            }
        }
    )
}

@Composable
fun EmptyDetails() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.notepad_logo),
            contentDescription = null,
            modifier = Modifier
                .height(512.dp)
                .width(512.dp)
                .alpha(0.5f)
        )
    }
}
