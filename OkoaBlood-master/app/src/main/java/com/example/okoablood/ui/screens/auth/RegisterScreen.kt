package com.example.okoablood.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.okoablood.ui.viewmodels.AuthViewModel


@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Authenticated) {
            onRegisterSuccess()
        }
    }

    val scrollState = rememberScrollState()

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthViewModel.AuthState.Authenticated -> {
                onNavigateToLogin() // make sure this navigates to an existing screen
            }
            is AuthViewModel.AuthState.Error -> {
                val error = (authState as AuthViewModel.AuthState.Error).message
                hasError = true
                errorMessage = error
            }
            else -> hasError = false
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "OkoaBlood",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Create an Account",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Name TextField
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; hasError = false },
                    label = { Text("Full Name") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Name"
                        )
                    },
                    isError = hasError && name.isBlank(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Email TextField
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; hasError = false },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email"
                        )
                    },
                    isError = hasError && email.isBlank(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Phone TextField
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; hasError = false },
                    label = { Text("Phone Number") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone"
                        )
                    },
                    isError = hasError && phone.isBlank(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )


                // Password TextField
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; hasError = false },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password"
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = hasError && password.isBlank(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Confirm Password TextField
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; hasError = false },
                    label = { Text("Confirm Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Confirm Password"
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = hasError && (confirmPassword.isBlank() || password != confirmPassword),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (hasError) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }


                //BloodGrup
                var expanded by remember { mutableStateOf(false) }
                val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
                var selectedBloodGroup by remember { mutableStateOf("") }


                Box(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .border(
                        width = 1.dp,
                        color = if (hasError && selectedBloodGroup.isBlank()) MaterialTheme.colorScheme.error else Color.Gray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(16.dp)
                ) {
                    Text(
                        text = if (selectedBloodGroup.isNotBlank()) selectedBloodGroup else "Select Blood Group",
                        color = if (selectedBloodGroup.isNotBlank()) Color.Unspecified else Color.Gray
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        bloodGroups.forEach { group ->
                            DropdownMenuItem(
                                text = { Text(text = group) },
                                onClick = {
                                    selectedBloodGroup = group
                                    expanded = false
                                    hasError = false
                                }
                            )
                        }
                    }
                }



                // Register Button
                Button(
                    onClick = {
                        when {
                            name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() -> {
                                hasError = true
                                errorMessage = "All fields are required"
                            }
                            password != confirmPassword -> {
                                hasError = true
                                errorMessage = "Passwords do not match"
                            }
                            password.length < 6 -> {
                                hasError = true
                                errorMessage = "Password must be at least 6 characters"
                            }
                            else -> {
                                viewModel.signUp(email, password, name, phone, bloodGroup = selectedBloodGroup,isDonor = false)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    enabled = authState !is AuthViewModel.AuthState.Loading
                ) {
                    if (authState is AuthViewModel.AuthState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Register")
                    }
                }

                // Login navigation text
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Login",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        }

                    )
                }
            }
        }
    }
}

