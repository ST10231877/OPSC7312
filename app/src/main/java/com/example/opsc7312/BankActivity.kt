package com.example.opsc7312

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class BankActivity : ComponentActivity() {

    private lateinit var btnHome: Button
    private lateinit var btnMainBankCreate: Button
    private lateinit var btnMainBankEdit: Button
    private lateinit var btnMainDeleteBank: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bank_page)

        btnHome = findViewById(R.id.btnHome)
        btnMainBankCreate = findViewById(R.id.btnMainBankCreate)
        btnMainBankEdit = findViewById(R.id.btnMainBankEdit)
        btnMainDeleteBank = findViewById(R.id.btnMainDeleteBank)

        btnHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        btnMainBankCreate.setOnClickListener {
            startActivity(Intent(this, BankCreateActivity::class.java))
        }

        btnMainBankEdit.setOnClickListener {
            startActivity(Intent(this, BankEditActivity::class.java))
        }

        btnMainDeleteBank.setOnClickListener {
            startActivity(Intent(this, BankDeleteActivity::class.java))
        }

    }
}