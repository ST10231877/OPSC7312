package com.example.opsc7312

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class SignUpActivity : ComponentActivity() {

    private lateinit var btnSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_page)

        btnSignUp = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

    }
}