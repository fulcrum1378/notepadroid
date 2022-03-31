package com.farmerbb.notepad.old.activity;

import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.farmerbb.notepad.R;

public abstract class NotepadBaseActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.navbar_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            getWindow().setNavigationBarDividerColor(ContextCompat.getColor(this,
                    R.color.navbar_divider_color));
    }
}
