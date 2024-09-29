package com.example.opsc7312

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class HomeActivity : ComponentActivity() {

    private lateinit var btnBank: Button
    private lateinit var btnBudget: Button
    private lateinit var btnSavings: Button
    private lateinit var btnSettings: Button
    private lateinit var btnLogout: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        btnBank = findViewById(R.id.btnBank)
        btnBudget = findViewById(R.id.btnBudget)
        btnSavings = findViewById(R.id.btnSavings)
        btnSettings = findViewById(R.id.btnSettings)
        btnLogout = findViewById(R.id.btnLogout)

        btnBank.setOnClickListener {
            startActivity(Intent(this, BankActivity::class.java))
        }

        btnBudget.setOnClickListener {
            startActivity(Intent(this, BudgetActivity::class.java))
        }

        btnSavings.setOnClickListener {
            startActivity(Intent(this, SavingsActivity::class.java))
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Set up the logout functionality
        btnLogout.setOnClickListener {
            logoutUser()  // Call logout function
        }

    }

    // Function to handle user logout
    private fun logoutUser() {
        // Clear SharedPreferences
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply() // Clear session data

        // Redirect to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear activity stack
        startActivity(intent)
        finish() // Finish HomeActivity
    }
}