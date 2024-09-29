package com.example.opsc7312

import LoginRequest
import LoginResponse
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.opsc7312.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest

class LoginActivity : ComponentActivity() {

    private lateinit var btnLogin: Button
    private lateinit var txtUsername: EditText
    private lateinit var txtPassword: EditText

    // SharedPreferences to store session data
    private lateinit var sharedPreferences: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        // Initialize UI components
        btnLogin = findViewById(R.id.btnLogin)
        txtUsername = findViewById(R.id.txtUsername)
        txtPassword = findViewById(R.id.txtPassword)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)

        btnLogin.setOnClickListener {
            val username = txtUsername.text.toString().trim()
            val password = txtPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Hash the password before sending to API
                val hashedPassword = hashPassword(password)
                loginUser(username, hashedPassword)
            }
        }
    }

    private fun loginUser(username: String, password: String) {
        val request = LoginRequest(username, password)
        val call = RetrofitClient.apiService.loginUser(request)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("LoginActivity", "API Response: $response")

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    Log.d("LoginActivity", "Response Body: $loginResponse")

                    if (loginResponse != null && loginResponse.message == "Login successful") {
                        // Save the userId to SharedPreferences
                        val userId = loginResponse.userId
                        sharedPreferences.edit().putString("userId", userId).apply()

                        // Login successful, navigate to HomeActivity
                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed: ${loginResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginActivity", "Failed to connect: ${t.message}")
                Toast.makeText(this@LoginActivity, "Failed to connect: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Function to hash password
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
