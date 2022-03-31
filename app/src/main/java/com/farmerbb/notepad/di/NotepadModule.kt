package com.farmerbb.notepad.di

import android.content.Context
import com.farmerbb.notepad.Database
import com.farmerbb.notepad.android.NotepadViewModel
import com.farmerbb.notepad.data.DataMigrator
import com.farmerbb.notepad.data.NotepadRepository
import com.farmerbb.notepad.models.NoteMetadata
import com.farmerbb.notepad.utils.dataStore
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import de.schnettler.datastore.manager.DataStoreManager
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.*

val notepadModule = module {
    viewModel { NotepadViewModel(androidApplication(), get(), get()) }
    single { provideDatabase(androidContext()) }
    single { NotepadRepository(get()) }
    single { DataMigrator(androidContext(), get()) }
    single { DataStoreManager(androidContext().dataStore) }
}

private fun provideDatabase(context: Context) = Database(
    driver = AndroidSqliteDriver(Database.Schema, context, "notepad.db"),
    NoteMetadataAdapter = NoteMetadata.Adapter(dateAdapter = DateAdapter)
)

object DateAdapter : ColumnAdapter<Date, Long> {
    override fun decode(databaseValue: Long) = Date(databaseValue)
    override fun encode(value: Date) = value.time
}
