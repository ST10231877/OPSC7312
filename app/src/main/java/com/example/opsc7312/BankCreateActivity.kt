package com.example.opsc7312

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.opsc7312.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BankCreateActivity : ComponentActivity() {

    private lateinit var txtBankName: EditText
    private lateinit var txtBankType: EditText
    private lateinit var txtBankDeposit: EditText
    private lateinit var btnBankCreate: Button
    private lateinit var btnHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bankcreate_page)

        // Initialize UI elements
        txtBankName = findViewById(R.id.txtBankName)
        txtBankType = findViewById(R.id.txtBankType)
        txtBankDeposit = findViewById(R.id.txtBankDeposit)
        btnBankCreate = findViewById(R.id.btnBankCreate)
        btnHome = findViewById(R.id.btnHome)

        btnHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }


    }
}
