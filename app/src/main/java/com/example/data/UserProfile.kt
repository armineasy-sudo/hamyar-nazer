package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * پروفایل مهندس ناظر
 */
@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "",
    val licenseNumber: String = "",
    val organizationName: String = "شرکت همتا انرژی سازان آتین",
    val phone: String = "",
    val email: String = "",
    val signatureText: String = ""
)
