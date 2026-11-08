package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

/**
 * نتیجه بررسی هر بند در یک بازدید
 */
@Entity(
    tableName = "inspection_item_results",
    foreignKeys = [
        ForeignKey(
            entity = InspectionVisitEntity::class,
            parentColumns = ["id"],
            childColumns = ["visitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class InspectionItemResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val visitId: Long,
    val checklistItemId: String,
    val status: String, // CONFIRMED, REJECTED, NEEDS_FIX, NOT_APPLICABLE, NOT_EXECUTED_YET, UNRELATED
    val inspectorNote: String? = null
)
