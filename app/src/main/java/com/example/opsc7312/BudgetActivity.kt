package com.example.opsc7312

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class BudgetActivity : ComponentActivity() {

    private lateinit var btnHome: Button
    private lateinit var btnMainBudgetCreate: Button
    private lateinit var btnMainEditBudget: Button
    private lateinit var btnMainDeleteBudget: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.budget_page)

        // Initialize UI elements
        initViews()

        // Set button click listeners
        setButtonListeners()
    }

    private fun initViews() {
        btnHome = findViewById(R.id.btnHome)
        btnMainBudgetCreate = findViewById(R.id.btnMainBudgetCreate)
        btnMainEditBudget = findViewById(R.id.btnMainEditBudget)
        btnMainDeleteBudget = findViewById(R.id.btnMainDeleteBudget)
    }

    private fun setButtonListeners() {
        btnHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        btnMainBudgetCreate.setOnClickListener {
            startActivity(Intent(this, BudgetCreateActivity::class.java))
        }

        btnMainEditBudget.setOnClickListener {
            startActivity(Intent(this, BudgetEditActivity::class.java))
        }

        btnMainDeleteBudget.setOnClickListener {
            startActivity(Intent(this, BudgetDeleteActivity::class.java))
        }
    }
}
