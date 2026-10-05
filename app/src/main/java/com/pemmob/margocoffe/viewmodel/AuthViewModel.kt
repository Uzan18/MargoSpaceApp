package com.pemmob.margocoffe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.pemmob.margocoffe.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(
            isAuthenticated = repository.isLoggedIn()
        )
    )

    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.Login -> login(
                email = event.email,
                password = event.password
            )

            is AuthEvent.Register -> register(
                email = event.email,
                name = event.name,
                phone = event.phone,
                password = event.password
            )

            AuthEvent.Logout -> logout()

            AuthEvent.ClearError -> {
                _uiState.update {
                    it.copy(errorMessage = null)
                }
            }
        }
    }

    private fun login(
        email: String,
        password: String
    ) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Email dan sandi wajib diisi."
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val result = repository.login(
                email = email.trim(),
                password = password
            )

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = false,
                            errorMessage = mapFirebaseError(exception)
                        )
                    }
                }
        }
    }

    private fun register(
        email: String,
        name: String,
        phone: String,
        password: String
    ) {
        if (
            email.isBlank() ||
            name.isBlank() ||
            phone.isBlank() ||
            password.isBlank()
        ) {
            _uiState.update {
                it.copy(
                    errorMessage = "Lengkapi semua data terlebih dahulu."
                )
            }
            return
        }

        if (password.length < 6) {
            _uiState.update {
                it.copy(
                    errorMessage = "Sandi minimal 6 karakter."
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val result = repository.register(
                email = email.trim(),
                name = name.trim(),
                phone = phone.trim(),
                password = password
            )

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = false,
                            errorMessage = mapFirebaseError(exception)
                        )
                    }
                }
        }
    }

    private fun logout() {
        repository.logout()

        _uiState.update {
            AuthUiState(
                isAuthenticated = false
            )
        }
    }

    private fun mapFirebaseError(exception: Throwable): String {
        return when (exception) {
            is FirebaseAuthUserCollisionException ->
                "Email tersebut sudah terdaftar."

            is FirebaseAuthInvalidUserException ->
                "Akun tidak ditemukan."

            is FirebaseAuthInvalidCredentialsException ->
                "Email atau sandi tidak valid."

            else ->
                exception.message ?: "Terjadi kesalahan saat autentikasi."
        }
    }
}