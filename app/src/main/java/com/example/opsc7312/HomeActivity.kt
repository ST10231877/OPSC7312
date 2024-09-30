package com.example.opsc7312

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity

class HomeActivity : ComponentActivity() {

    // Declaring the buttons
    private lateinit var btnBank: Button
    private lateinit var btnBudget: Button
    private lateinit var btnSavings: Button
    private lateinit var btnSettings: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        // Initialize buttons
        initButtons()

        // Set button click listeners
        setupButtonListeners()
    }

    // Initialize button views
    private fun initButtons() {
        btnBank = findViewById(R.id.btnBank)
        btnBudget = findViewById(R.id.btnBudget)
        btnSavings = findViewById(R.id.btnSavings)
        btnSettings = findViewById(R.id.btnSettings)
        btnLogout = findViewById(R.id.btnLogout)
    }

    // Setup click listeners for the buttons
    private fun setupButtonListeners() {
        btnBank.setOnClickListener {
            navigateToActivity(BankActivity::class.java)
        }

        btnBudget.setOnClickListener {
            navigateToActivity(BudgetActivity::class.java)
        }

        btnSavings.setOnClickListener {
            showFeatureUnderDevelopment()
        }

        btnSettings.setOnClickListener {
            navigateToActivity(SettingsActivity::class.java)
        }

        btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    // Navigate to another activity
    private fun <T> navigateToActivity(activity: Class<T>) {
        startActivity(Intent(this, activity))
    }

    // Show a toast for features under development
    private fun showFeatureUnderDevelopment() {
        Toast.makeText(this, "This feature is under development. Come back soon!", Toast.LENGTH_SHORT).show()
    }

    // Function to handle user logout
    private fun logoutUser() {
        // Clear SharedPreferences
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply() // Clear session data

        // Redirect to LoginActivity
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear activity stack
        }
        startActivity(intent)
        finish() // Finish HomeActivity
    }
}
