package com.example.cookup.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.cookup.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.login_nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
