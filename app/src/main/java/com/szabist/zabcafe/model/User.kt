package com.szabist.zabcafe.model

import java.io.Serializable

data class User(
    val userId: String = "",
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val role: String = "",
    val contactNumber: String = "",
    val emailValid: Boolean = true,  // non-nullable, treated as a regular field
    val profileComplete: Boolean = true  // non-nullable, treated as a regular field
) : Serializable