package com.example.opsc7312

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class HomeActivity : ComponentActivity() {

    private lateinit var btnBank: Button
    private lateinit var btnBudget: Button
    private lateinit var btnSavings: Button
    private lateinit var btnSettings: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        btnBank = findViewById(R.id.btnBank)
        btnBudget = findViewById(R.id.btnBudget)
        btnSavings = findViewById(R.id.btnSavings)
        btnSettings = findViewById(R.id.btnSettings)

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

    }
}