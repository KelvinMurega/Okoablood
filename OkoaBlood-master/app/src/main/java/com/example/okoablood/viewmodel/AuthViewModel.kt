package com.example.okoablood.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.okoablood.data.model.Donor
import com.example.okoablood.data.model.User
import com.example.okoablood.data.remote.FirebaseService
import com.example.okoablood.data.repository.BloodDonationRepository
import com.example.okoablood.di.DependencyProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val firebaseService: FirebaseService = DependencyProvider.firebaseService,
    private val repository: BloodDonationRepository = DependencyProvider.repository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            val firebaseUser = repository.getCurrentUser()
            if (firebaseUser != null) {
                fetchUserData(firebaseUser.uid)
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun signIn(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val firebaseUser = repository.signIn(email, password)
                if (firebaseUser != null) {
                    fetchUserData(firebaseUser.uid)
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error("Authentication failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun signUp(
        email: String,
        password: String,
        name: String,
        phone: String,
        bloodGroup: String,
        isDonor: Boolean
    ) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val firebaseUser = repository.signUp(email, password)
                if (firebaseUser != null) {
                    val user = User(
                        id = firebaseUser.uid,
                        name = name,
                        email = email,
                        phoneNumber = phone,
                        bloodGroup = bloodGroup,
                        isDonor = false
                    )
                    repository.createUser(user)
                    if (isDonor) {
                        val donor = Donor(
                            id = firebaseUser.uid,
                            name = name,
                            bloodGroup = bloodGroup,
                            location = "Nairobi", // you can later include a location picker
                            phoneNumber = phone,
                            availableForDonation = true
                        )
                        repository.registerDonor(donor)
                    }


                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error("Registration failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun signOut() {
        repository.signOut()
        _currentUser.value = null
        _authState.value = AuthState.Unauthenticated
    }

    fun resetAuthState() {
        _authState.value = AuthState.Unauthenticated
    }

    private fun fetchUserData(userId: String) {
        viewModelScope.launch {
            try {
                val userData = repository.getUser(userId)
                if (userData != null) {
                    _currentUser.value = userData
                } else {
                    _authState.value = AuthState.Error("User profile not found")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error fetching user: ${e.message}")
            }
        }
    }


    fun updateUserProfile(user: User) {
        viewModelScope.launch {
            try {
                repository.updateUser(user)
                _currentUser.value = user
            } catch (_: Exception) {
                // You may handle error here if needed
            }
        }
    }

    sealed class AuthState {
        object Initial : AuthState()
        object Loading : AuthState()
        object Authenticated : AuthState()
        object Unauthenticated : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
