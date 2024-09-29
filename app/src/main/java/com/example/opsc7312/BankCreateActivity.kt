package com.example.opsc7312

import AddAccountRequest
import AddAccountResponse
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bankcreate_page)

        // Initialize UI elements
        txtBankName = findViewById(R.id.txtBankName)
        txtBankType = findViewById(R.id.txtBankType)
        txtBankDeposit = findViewById(R.id.txtBankDeposit)
        btnBankCreate = findViewById(R.id.btnBankCreate)
        btnHome = findViewById(R.id.btnHome)

        // Get SharedPreferences instance
        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)

        btnHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        btnBankCreate.setOnClickListener {
            val bankName = txtBankName.text.toString().trim()
            val bankType = txtBankType.text.toString().trim()
            val bankDeposit = txtBankDeposit.text.toString().trim().toDoubleOrNull()

            if (bankName.isEmpty() || bankType.isEmpty() || bankDeposit == null || bankDeposit <= 0) {
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            } else {
                // Retrieve userId from SharedPreferences
                val userId = sharedPreferences.getString("userId", null)

                if (userId != null) {
                    createAccount(userId, bankName, bankType, bankDeposit)
                } else {
                    Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createAccount(userId: String, accountName: String, accountType: String, amount: Double) {
        val request = AddAccountRequest(
            name = accountName,
            type = accountType,
            amount = amount,
            budgets = emptyList() // Initial empty list for budgets
        )

        // Call the API to create the account
        val call = RetrofitClient.apiService.addAccount(userId, request)
        call.enqueue(object : Callback<AddAccountResponse> {
            override fun onResponse(call: Call<AddAccountResponse>, response: Response<AddAccountResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@BankCreateActivity, "Account created successfully!", Toast.LENGTH_SHORT).show()

                    // Pass the accountName and userId to BudgetCreateActivity
                    val intent = Intent(this@BankCreateActivity, BudgetCreateActivity::class.java)
                    intent.putExtra("accountName", accountName)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@BankCreateActivity, "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AddAccountResponse>, t: Throwable) {
                Toast.makeText(this@BankCreateActivity, "Failed to connect: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
