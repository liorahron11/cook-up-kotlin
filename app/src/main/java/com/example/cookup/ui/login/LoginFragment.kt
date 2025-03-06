package com.example.cookup.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cookup.R

@Composable
fun LoginFragment(viewModel: LoginViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .background(MaterialTheme.colors.surface, shape = RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.cookup_logo),
                contentDescription = "CookUp Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "CookUp",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = viewModel.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("אימייל", color = MaterialTheme.colors.onSurface) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    textColor = MaterialTheme.colors.onSurface,
                    focusedIndicatorColor = MaterialTheme.colors.onSurface,
                    unfocusedIndicatorColor = MaterialTheme.colors.onSurface
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = viewModel.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("סיסמה", color = MaterialTheme.colors.onSurface) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    textColor = MaterialTheme.colors.onSurface,
                    focusedIndicatorColor = MaterialTheme.colors.onSurface,
                    unfocusedIndicatorColor = MaterialTheme.colors.onSurface
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Handle Login */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary) // White button from theme
            ) {
                Text("התחבר", color = MaterialTheme.colors.onPrimary, fontSize = 18.sp)
            }
        }
    }
}
