package com.szabist.zabcafe.data.dto

import java.io.Serializable

data class UserDTO(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val role: String = "",  // Example roles could be "admin", "student", "faculty", etc.
    val contactNumber: String = "",
    val emailValid: Boolean? = true,  // nullable
    val profileComplete: Boolean? = true  // nullable
) : Serializable