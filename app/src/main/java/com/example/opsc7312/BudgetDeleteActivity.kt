package com.example.opsc7312


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.example.opsc7312.api.AccountsResponse
import com.example.opsc7312.api.CategoriesResponse
import com.example.opsc7312.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class BudgetDeleteActivity : ComponentActivity() {

    private lateinit var btnHome: Button
    private lateinit var btnDeleteBudget: Button
    private lateinit var spinnerAccounts: Spinner
    private lateinit var spinnerCategories: Spinner
    private lateinit var noCategoriesTextView: TextView

    private var accountNames: List<String> = emptyList()
    private var categoryNames: List<String> = emptyList()

    private var selectedAccount: String? = null
    private var selectedCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.budgetdelete_page)

        // Initialize UI elements
        btnHome = findViewById(R.id.btnHome)
        btnDeleteBudget = findViewById(R.id.btnDeleteBudget)
        spinnerAccounts = findViewById(R.id.spinnerAccounts)
        spinnerCategories = findViewById(R.id.spinnerCategories)
        noCategoriesTextView = findViewById(R.id.noCategoriesTextView) // TextView to display when no categories are found

        // Go back to home
        btnHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        // Retrieve userId from SharedPreferences
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        if (userId != null) {
            fetchUserAccounts(userId)
        } else {
            Toast.makeText(this, "User session is missing. Please log in again.", Toast.LENGTH_SHORT).show()
        }

        // Handle account selection
        spinnerAccounts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedAccount = accountNames[position]
                // Fetch categories for the selected account
                if (selectedAccount != null && userId != null) {
                    fetchAccountCategories(userId, selectedAccount!!)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedAccount = null
            }
        }

        // Handle category selection
        spinnerCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCategory = categoryNames[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedCategory = null
            }
        }

        // Handle delete button click
        btnDeleteBudget.setOnClickListener {
            if (selectedAccount != null && selectedCategory != null && userId != null) {
                // Show confirmation dialog before deleting
                showDeleteConfirmationDialog(userId, selectedAccount!!, selectedCategory!!)
            } else {
                Toast.makeText(this, "Please select an account and category", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Show confirmation dialog before deleting the budget category
    // Show confirmation dialog before deleting the account
    private fun showDeleteConfirmationDialog(userId: String, accountName: String, category: String) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Delete Account")
        builder.setMessage("Are you sure you want to delete the account '$accountName'?")

        // Set up the confirmation buttons
        builder.setPositiveButton("Yes") { dialog, _ ->
            deleteCategory(userId, accountName, category) // Proceed with account deletion
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Cancel the deletion process
        }

        val dialog: android.app.AlertDialog = builder.create()
        dialog.show()
    }

    private fun deleteCategory(userId: String, accountName: String, category: String) {
        val call = RetrofitClient.apiService.deleteCategory(userId, accountName, category)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@BudgetDeleteActivity, "Category '$category' deleted successfully.", Toast.LENGTH_SHORT).show()
                    // After deletion, refresh the category spinner
                    fetchAccountCategories(userId, accountName)
                } else {
                    Toast.makeText(this@BudgetDeleteActivity, "Failed to delete category: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@BudgetDeleteActivity, "Failed to connect: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
                        this@BudgetDeleteActivity,
                        android.R.layout.simple_spinner_item,
                        accountNames
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerAccounts.adapter = adapter
                } else {
                    Toast.makeText(this@BudgetDeleteActivity, "Failed to fetch accounts", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AccountsResponse>, t: Throwable) {
                Toast.makeText(this@BudgetDeleteActivity, "Failed to connect: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchAccountCategories(userId: String, accountName: String) {
        val call = RetrofitClient.apiService.getAccountCategories(userId, accountName)
        call.enqueue(object : Callback<CategoriesResponse> {  // Changed to CategoriesResponse
            override fun onResponse(call: Call<CategoriesResponse>, response: Response<CategoriesResponse>) {
                if (response.isSuccessful) {
                    val categoriesResponse = response.body()

                    if (categoriesResponse != null && categoriesResponse.budgets.isNotEmpty()) {
                        noCategoriesTextView.visibility = TextView.GONE
                        categoryNames = categoriesResponse.budgets.map { it.category }  // Extract categories

                        // Populate spinner with category names
                        val adapter = ArrayAdapter(this@BudgetDeleteActivity, android.R.layout.simple_spinner_item, categoryNames)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerCategories.adapter = adapter
                        btnDeleteBudget.isEnabled = true // Enable delete button
                    } else {
                        noCategoriesTextView.text = "No categories found for this account."
                        noCategoriesTextView.visibility = TextView.VISIBLE
                        categoryNames = emptyList()
                        btnDeleteBudget.isEnabled = false // Disable delete button
                    }
                } else {
                    Toast.makeText(this@BudgetDeleteActivity, "Failed to fetch categories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
                Toast.makeText(this@BudgetDeleteActivity, "Failed to connect: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}


