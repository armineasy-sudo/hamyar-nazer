package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "checklist_items",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId"])]
)
data class ChecklistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val category: String, // BEFORE_CONSTRUCTION, DURING_CONSTRUCTION, BEFORE_FINAL_APPROVAL
    val title: String, // Persian description
    val status: String, // COMPLETED, NOT_COMPLETED, REQUIRES_CORRECTION
    val inspectorNotes: String = "",
    val priority: String = "MEDIUM", // CRITICAL, HIGH, MEDIUM, LOW
    val regulationReference: String = "", // e.g. مبحث ۱۴ مقررات ملی
    val photoPath: String? = null // Simulated local path
)
