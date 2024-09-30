package com.example.opsc7312

import com.example.opsc7312.api.AccountsResponse
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.opsc7312.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BankActivity : ComponentActivity() {

    private lateinit var btnHome: Button
    private lateinit var btnMainBankCreate: Button
    private lateinit var btnMainBankEdit: Button
    private lateinit var btnMainDeleteBank: Button
    private lateinit var spnAccounts: Spinner

    private var accountNames: List<String> = emptyList() // To store account names
    private var selectedAccount: String? = null // To store selected account name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bank_page)

        btnHome = findViewById(R.id.btnHome)
        btnMainBankCreate = findViewById(R.id.btnMainBankCreate)
        btnMainBankEdit = findViewById(R.id.btnMainBankEdit)
        btnMainDeleteBank = findViewById(R.id.btnMainDeleteBank)
        spnAccounts = findViewById(R.id.spnAccounts)

        // Retrieve userId from SharedPreferences
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        if (userId != null) {
            fetchUserAccounts(userId)
        } else {
            Toast.makeText(
                this,
                "User session is missing. Please log in again.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Set up spinner selection listener
        spnAccounts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedAccount = accountNames[position] // Store the selected account
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedAccount = null
            }
        }

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

    private fun fetchUserAccounts(userId: String) {
        val call = RetrofitClient.apiService.getUserAccounts(userId) // Example API call
        call.enqueue(object : Callback<AccountsResponse> { // Change List<Account> to AccountsResponse
            override fun onResponse(call: Call<AccountsResponse>, response: Response<AccountsResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val accountsResponse = response.body()!!
                    val accounts = accountsResponse.accounts // Extract accounts from the response

                    accountNames = accounts.map { it.name } // Get list of account names

                    // Populate the spinner with account names
                    val adapter = ArrayAdapter(
                        this@BankActivity,
                        android.R.layout.simple_spinner_item,
                        accountNames
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spnAccounts.adapter = adapter
                } else {
                    Toast.makeText(
                        this@BankActivity,
                        "Failed to fetch accounts",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<AccountsResponse>, t: Throwable) {
                Toast.makeText(this@BankActivity, "Failed to connect: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}