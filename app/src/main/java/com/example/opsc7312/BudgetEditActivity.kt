package com.example.opsc7312

import com.example.opsc7312.api.AccountsResponse
import com.example.opsc7312.api.UpdateBudgetAmountResponse
import com.example.opsc7312.api.UpdateSpentAmountResponse
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import com.example.opsc7312.api.RetrofitClient
import com.example.opsc7312.api.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BudgetEditActivity : ComponentActivity() {

    private lateinit var btnEditBudget: Button
    private lateinit var spinnerAccounts: Spinner
    private lateinit var txtCategoryName: EditText
    private lateinit var txtAmountBudgeted: EditText
    private lateinit var txtAmountSpent: EditText

    private var accountNames: List<String> = emptyList() // To store account names
    private var selectedAccount: String? = null // To store selected account name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.budgetedit_page)

        // Initialize UI elements
        spinnerAccounts = findViewById(R.id.spinnerAccounts)
        txtCategoryName = findViewById(R.id.txtCategoryName)
        txtAmountBudgeted = findViewById(R.id.txtAmountBudgeted)
        txtAmountSpent = findViewById(R.id.txtAmountSpent)
        btnEditBudget = findViewById(R.id.btnEditBudget)

        // Retrieve userId from SharedPreferences
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        if (userId != null) {
            fetchUserAccounts(userId)
        } else {
            Toast.makeText(this, "User session is missing. Please log in again.", Toast.LENGTH_SHORT).show()
        }

        // Set up spinner selection listener
        spinnerAccounts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedAccount = accountNames[position] // Store the selected account
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedAccount = null
            }
        }

        // Edit budget button listener
        btnEditBudget.setOnClickListener {
            val category = txtCategoryName.text.toString().trim()
            val amountBudgeted = txtAmountBudgeted.text.toString().trim().toDoubleOrNull()
            val amountSpent = txtAmountSpent.text.toString().trim().toDoubleOrNull()

            if (category.isEmpty() || selectedAccount == null || amountBudgeted == null || amountSpent == null) {
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            } else {
                if (userId != null && selectedAccount != null) {
                    updateBudget(userId, selectedAccount!!, category, amountBudgeted, amountSpent)
                }
            }
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

                    val adapter = ArrayAdapter(
                        this@BudgetEditActivity,
                        android.R.layout.simple_spinner_item,
                        accountNames
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerAccounts.adapter = adapter
                } else {
                    Toast.makeText(this@BudgetEditActivity, "Failed to fetch accounts", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AccountsResponse>, t: Throwable) {
                Toast.makeText(this@BudgetEditActivity, "Failed to connect: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateBudget(userId: String, accountName: String, category: String, amountBudgeted: Double, amountSpent: Double) {
        // Update the budgeted amount
        val updateBudgetAmountCall = RetrofitClient.apiService.editBudgetAmount(userId, accountName, category, amountBudgeted)
        updateBudgetAmountCall.enqueue(object : Callback<UpdateBudgetAmountResponse> {
            override fun onResponse(call: Call<UpdateBudgetAmountResponse>, response: Response<UpdateBudgetAmountResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val budgetResponse = response.body()!!
                    Toast.makeText(this@BudgetEditActivity, budgetResponse.message, Toast.LENGTH_SHORT).show()

                    // Now update the spent amount
                    updateSpentAmount(userId, accountName, category, amountSpent)
                } else {
                    Toast.makeText(this@BudgetEditActivity, "Failed to update budget amount", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateBudgetAmountResponse>, t: Throwable) {
                Toast.makeText(this@BudgetEditActivity, "Failed to connect: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateSpentAmount(userId: String, accountName: String, category: String, amountSpent: Double) {
        val updateSpentAmountCall = RetrofitClient.apiService.editSpentAmount(userId, accountName, category, amountSpent)
        updateSpentAmountCall.enqueue(object : Callback<UpdateSpentAmountResponse> {
            override fun onResponse(call: Call<UpdateSpentAmountResponse>, response: Response<UpdateSpentAmountResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val spentResponse = response.body()!!
                    Toast.makeText(this@BudgetEditActivity, spentResponse.message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@BudgetEditActivity, "Failed to update spent amount", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateSpentAmountResponse>, t: Throwable) {
                Toast.makeText(this@BudgetEditActivity, "Failed to connect: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
