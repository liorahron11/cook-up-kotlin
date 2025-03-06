package com.example.cookup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import com.example.cookup.ui.login.LoginViewModel
import com.example.cookup.ui.theme.CookUpTheme
import com.example.cookup.ui.login.LoginFragment
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CookUpTheme {
                val systemUiController = rememberSystemUiController()
                val statusBarColor = MaterialTheme.colors.primary

                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = statusBarColor,
                        darkIcons = false
                    )
                }

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.surface
                    ) {
                        val loginViewModel: LoginViewModel = LoginViewModel()
                        LoginFragment(loginViewModel)
                    }
                }
            }
        }
    }
}
