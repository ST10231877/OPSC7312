package com.example.opsc7312

import Account
import AccountsResponse
import AddCategoryRequest
import AddCategoryResponse
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import com.example.opsc7312.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BudgetCreateActivity : ComponentActivity() {

    private lateinit var txtCategory: EditText
    private lateinit var txtAmountBudgeted: EditText
    private lateinit var btnBudgetCreate: Button
    private lateinit var spinnerAccounts: Spinner

    private var accountNames: List<String> = emptyList() // To store account names
    private var selectedAccount: String? = null // To store selected account name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.budgetcreate_page)

        // Initialize UI elements
        txtCategory = findViewById(R.id.txtCategory)
        txtAmountBudgeted = findViewById(R.id.txtAmountBudgeted)
        btnBudgetCreate = findViewById(R.id.btnBudgetCreate)
        spinnerAccounts = findViewById(R.id.spinnerAccounts)

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
        spinnerAccounts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        // Budget creation button listener
        btnBudgetCreate.setOnClickListener {
            val category = txtCategory.text.toString().trim()
            val amountBudgeted = txtAmountBudgeted.text.toString().trim().toDoubleOrNull()

            if (category.isEmpty() || amountBudgeted == null || amountBudgeted <= 0 || selectedAccount == null) {
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (userId != null && selectedAccount != null) {
                    addCategoryToAccount(userId, selectedAccount!!, category, amountBudgeted)
                }
            }
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
                        this@BudgetCreateActivity,
                        android.R.layout.simple_spinner_item,
                        accountNames
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerAccounts.adapter = adapter
                } else {
                    Toast.makeText(
                        this@BudgetCreateActivity,
                        "Failed to fetch accounts",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<AccountsResponse>, t: Throwable) {
                Toast.makeText(this@BudgetCreateActivity, "Failed to connect: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addCategoryToAccount(
        userId: String,
        accountName: String,
        category: String,
        amountBudgeted: Double
    ) {
        val request = AddCategoryRequest(
            category = category,
            amountBudgeted = amountBudgeted,
            amountSpent = 0.0 // Initial amount spent is zero
        )

        val call = RetrofitClient.apiService.addCategory(userId, accountName, request)
        call.enqueue(object : Callback<AddCategoryResponse> {
            override fun onResponse(
                call: Call<AddCategoryResponse>,
                response: Response<AddCategoryResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@BudgetCreateActivity,
                        "Budget created successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e("BudgetCreateActivity", "Error: ${response.errorBody()?.string()}")
                    Toast.makeText(
                        this@BudgetCreateActivity,
                        "Error: ${response.errorBody()?.string()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<AddCategoryResponse>, t: Throwable) {
                Log.e("BudgetCreateActivity", "Failed to connect: ${t.message}")
                Toast.makeText(
                    this@BudgetCreateActivity,
                    "Failed to connect: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
