package com.cashbox.android.data.model

data class LoginBody(
    val email: String,
    val password: String
)

data class LoginGoogleBody(
    val idToken: String
)

data class RegisterBody(
    val name: String,
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: UserData
)

data class RegisterResponse(
    val message: String,
    val user: UserData
)

data class UserData(
    val uid: String,
    val email: String,
    val name: String
)

data class AuthError(
    val message: String
)