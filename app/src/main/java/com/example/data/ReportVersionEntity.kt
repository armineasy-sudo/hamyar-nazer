package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

/**
 * نسخه گزارش
 * هر ویرایش یک نسخه جدید ایجاد می‌کند.
 */
@Entity(
    tableName = "report_versions",
    foreignKeys = [
        ForeignKey(
            entity = InspectionVisitEntity::class,
            parentColumns = ["id"],
            childColumns = ["visitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReportVersionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val visitId: Long,
    val versionNumber: Int,
    val content: String,
    val tone: String, // NORMAL, SERIOUS, SAFETY_WARNING
    val includeGas: Boolean = false,
    val municipalRegistrationNumber: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isFinal: Boolean = false
)
