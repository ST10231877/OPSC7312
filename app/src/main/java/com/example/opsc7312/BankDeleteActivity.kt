package com.example.opsc7312

import AccountsResponse
import DeleteAccountResponse
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import com.example.opsc7312.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BankDeleteActivity : ComponentActivity() {

    private lateinit var spinnerAccounts: Spinner
    private lateinit var btnBankDelete: Button
    private var accountNames: List<String> = emptyList() // Store account names
    private var selectedAccount: String? = null // Store selected account name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bankdelete_page)

        // Initialize UI elements
        spinnerAccounts = findViewById(R.id.spnBankAccount)
        btnBankDelete = findViewById(R.id.btnBankDelete)

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

        // Delete account button listener with confirmation dialog
        btnBankDelete.setOnClickListener {
            if (selectedAccount != null && userId != null) {
                showDeleteConfirmationDialog(userId, selectedAccount!!)
            } else {
                Toast.makeText(this, "Please select an account to delete.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fetch user accounts from the API
    private fun fetchUserAccounts(userId: String) {
        val call = RetrofitClient.apiService.getUserAccounts(userId)
        call.enqueue(object : Callback<AccountsResponse> {
            override fun onResponse(call: Call<AccountsResponse>, response: Response<AccountsResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val accountsResponse = response.body()!!
                    accountNames = accountsResponse.accounts.map { it.name }

                    // Populate the spinner with account names
                    val adapter = ArrayAdapter(
                        this@BankDeleteActivity,
                        android.R.layout.simple_spinner_item,
                        accountNames
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerAccounts.adapter = adapter
                } else {
                    Toast.makeText(this@BankDeleteActivity, "Failed to fetch accounts", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AccountsResponse>, t: Throwable) {
                Toast.makeText(this@BankDeleteActivity, "Failed to connect: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Show confirmation dialog before deleting the account
    private fun showDeleteConfirmationDialog(userId: String, accountName: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Account")
        builder.setMessage("Are you sure you want to delete the account '$accountName'?")

        // Set up the confirmation buttons
        builder.setPositiveButton("Yes") { dialog, _ ->
            deleteAccount(userId, accountName) // Proceed with account deletion
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Cancel the deletion process
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // Delete the selected account
    private fun deleteAccount(userId: String, accountName: String) {
        val call = RetrofitClient.apiService.deleteAccount(userId, accountName)
        call.enqueue(object : Callback<DeleteAccountResponse> {
            override fun onResponse(call: Call<DeleteAccountResponse>, response: Response<DeleteAccountResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val deleteResponse = response.body()!!
                    Toast.makeText(this@BankDeleteActivity, deleteResponse.message, Toast.LENGTH_SHORT).show()

                    // Refresh the spinner after deletion
                    fetchUserAccounts(userId)
                } else {
                    Toast.makeText(this@BankDeleteActivity, "Failed to delete account", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DeleteAccountResponse>, t: Throwable) {
                Toast.makeText(this@BankDeleteActivity, "Failed to connect: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
