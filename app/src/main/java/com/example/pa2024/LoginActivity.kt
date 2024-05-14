package com.example.pa2024

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Set window insets listener
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_button)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
            val url = "https://api.au-temps-donne.nicolas-guillot.fr/api/login"

            val params = HashMap<String, String>()
            params["email"] = email
            params["password"] = password
            val jsonObject = JSONObject(params as Map<*, *>)

            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObject,
                { response ->
                    try {
                        val token = response.getString("token")

                        // Store the token in SharedPreferences
                        val sharedPreferences = getSharedPreferences("com.example.pa2024.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString("TOKEN_KEY", token)
                            apply()
                        }

                        Toast.makeText(this, "Login successful. Token saved.", Toast.LENGTH_LONG).show()

                        // Open MainActivity after a successful login
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Optional: Close the login activity so it's not accessible via back button
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "Unexpected response format", Toast.LENGTH_SHORT).show()
                    }
                },
                { _ ->
                    Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
                })

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest)
        }
    }
}
