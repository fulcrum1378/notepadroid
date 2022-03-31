package com.farmerbb.notepad.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.farmerbb.notepad.data.DataMigrator
import com.farmerbb.notepad.ui.routes.NotepadComposeApp
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class NotepadActivity : ComponentActivity() {
    private val migrator: DataMigrator = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            migrator.migrate()
            setContent { NotepadComposeApp() }
        }
    }
}
