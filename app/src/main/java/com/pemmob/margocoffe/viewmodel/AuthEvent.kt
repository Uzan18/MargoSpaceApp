package com.pemmob.margocoffe.viewmodel

sealed interface AuthEvent {

    data class Login(
        val email: String,
        val password: String
    ) : AuthEvent

    data class Register(
        val email: String,
        val name: String,
        val phone: String,
        val password: String
    ) : AuthEvent

    data object Logout : AuthEvent

    data object ClearError : AuthEvent
}