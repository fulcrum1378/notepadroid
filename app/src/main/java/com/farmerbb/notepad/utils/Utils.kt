package com.farmerbb.notepad.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.datastore.preferences.preferencesDataStore
import com.farmerbb.notepad.BuildConfig
import java.text.DateFormat
import java.util.*

fun Context.showToast(
    @StringRes text: Int
) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

val Context.dataStore by preferencesDataStore("settings")

val buildYear: Int
    get() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Denver")).apply {
            timeInMillis = BuildConfig.TIMESTAMP
        }

        return calendar.get(Calendar.YEAR)
    }

val Date.noteListFormat: String
    get() = DateFormat
        .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
        .format(this)
