package com.szabist.zabcafe.model

import java.io.Serializable


data class User(
    val userId: String = "",
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val role: String = "",
    val contactNumber: String = ""
) : Serializable {


    fun isProfileComplete(): Boolean {
        return username.isNotBlank() && email.isNotBlank() && contactNumber.isNotBlank() && role.isNotBlank()
    }


    fun isEmailValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}