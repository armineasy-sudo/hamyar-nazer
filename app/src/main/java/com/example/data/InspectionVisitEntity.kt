package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

/**
 * بازدید مستقل هر پروژه
 * تاریخ بازدید شمسی و مستقل از تاریخ گزارش است.
 */
@Entity(
    tableName = "inspection_visits",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class InspectionVisitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val visitDateShamsi: String, // تاریخ شمسی
    val visitTime: String? = null, // اختیاری
    val executionStage: String, // مرحله اجرا
    val notes: String? = null,
    val status: String = "DRAFT", // DRAFT, COMPLETED
    val createdAt: Long = System.currentTimeMillis()
)
