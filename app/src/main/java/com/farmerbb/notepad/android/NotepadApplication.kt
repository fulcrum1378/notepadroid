package com.farmerbb.notepad.android

import android.app.Application
import com.farmerbb.notepad.di.notepadModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class NotepadApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NotepadApplication)
            modules(notepadModule)
        }
    }
}
