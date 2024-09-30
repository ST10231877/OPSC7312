package com.example.opsc7312


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.opsc7312.api.AccountsResponse
import com.example.opsc7312.api.RetrofitClient
import com.example.opsc7312.api.UpdateBalanceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BankEditActivity : ComponentActivity() {
    private lateinit var btnHome: Button
    private lateinit var btnEdit: Button
    private lateinit var txtNewBalance: EditText
    private lateinit var spnBankAccount: Spinner
    private lateinit var userId: String // Keep it non-nullable

    private var accountNames: List<String> = emptyList()
    private var selectedAccount: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bankedit_page)

        btnHome = findViewById(R.id.btnHome)
        btnEdit = findViewById(R.id.btnBankEdit)
        txtNewBalance = findViewById(R.id.txtNewBalance) // Make sure this ID matches your layout
        spnBankAccount = findViewById(R.id.spnBankAccount)

        // Retrieve userId from SharedPreferences
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val retrievedUserId = sharedPreferences.getString("userId", null)

        if (retrievedUserId != null) {
            userId = retrievedUserId // Initialize the non-nullable userId
            fetchUserAccounts(userId)
        } else {
            Toast.makeText(this, "User session is missing. Please log in again.", Toast.LENGTH_SHORT).show()
            finish() // Optionally close the activity if no userId
        }

        // Set up spinner selection listener
        spnBankAccount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedAccount = accountNames[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedAccount = null
            }
        }

        btnHome.setOnClickListener{
            startActivity(Intent(this, HomeActivity::class.java))
        }

        btnEdit.setOnClickListener {
            updateAccountBalance()
        }
    }

    private fun fetchUserAccounts(userId: String) {
        val call = RetrofitClient.apiService.getUserAccounts(userId)
        call.enqueue(object : Callback<AccountsResponse> {
            override fun onResponse(call: Call<AccountsResponse>, response: Response<AccountsResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val accountsResponse = response.body()!!
                    val accounts = accountsResponse.accounts

                    accountNames = accounts.map { it.name }
                    val adapter = ArrayAdapter(this@BankEditActivity, android.R.layout.simple_spinner_item, accountNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spnBankAccount.adapter = adapter
                } else {
                    Toast.makeText(this@BankEditActivity, "Failed to fetch accounts", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AccountsResponse>, t: Throwable) {
                Toast.makeText(this@BankEditActivity, "Failed to connect: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateAccountBalance() {
        val accountName = spnBankAccount.selectedItem?.toString() ?: return
        val newBalance = txtNewBalance.text.toString().toDoubleOrNull()

        if (newBalance != null) {
            val call = RetrofitClient.apiService.updateAccountBalance(userId, accountName, newBalance)
            call.enqueue(object : Callback<UpdateBalanceResponse> {
                override fun onResponse(call: Call<UpdateBalanceResponse>, response: Response<UpdateBalanceResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(this@BankEditActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@BankEditActivity, "Failed to update balance", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UpdateBalanceResponse>, t: Throwable) {
                    Toast.makeText(this@BankEditActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Please enter a valid balance", Toast.LENGTH_SHORT).show()
        }
    }
}
