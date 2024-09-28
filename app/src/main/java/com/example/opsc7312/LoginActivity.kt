package com.example.opsc7312

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class LoginActivity : ComponentActivity() {

    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }


        }
}