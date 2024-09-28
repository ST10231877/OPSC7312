package com.example.opsc7312

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class SavingsActivity : ComponentActivity() {

    private lateinit var btnHome: Button
    private lateinit var btnMainCreateSavings: Button
    private lateinit var btnMainEditSavings: Button
    private lateinit var btnMainDeleteSavings: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.savings_page)

        btnHome = findViewById(R.id.btnHome)
        btnMainCreateSavings = findViewById(R.id.btnMainCreateSavings)
        btnMainEditSavings = findViewById(R.id.btnMainEditSavings)
        btnMainDeleteSavings = findViewById(R.id.btnMainDeleteSavings)

        btnHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        btnMainCreateSavings.setOnClickListener {
            startActivity(Intent(this, SavingsCreateActivity::class.java))
        }

        btnMainEditSavings.setOnClickListener {
            startActivity(Intent(this, SavingsEditActivity::class.java))
        }

        btnMainDeleteSavings.setOnClickListener {
            startActivity(Intent(this, SavingsDeleteActivity::class.java))
        }

    }
}