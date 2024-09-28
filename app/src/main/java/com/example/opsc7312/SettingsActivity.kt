package com.example.opsc7312

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class SettingsActivity : ComponentActivity() {
    private lateinit var btnHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_page)

        btnHome = findViewById(R.id.btnHome)

        btnHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }


    }
}