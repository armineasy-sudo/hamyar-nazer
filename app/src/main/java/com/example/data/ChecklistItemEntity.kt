package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * بند چک‌لیست پایه (رسمی)
 * شناسه یکتا ثابت می‌ماند تا تاریخچه حفظ شود.
 */
@Entity(tableName = "checklist_items")
data class ChecklistItemEntity(
    @PrimaryKey val id: String, // مثال: CL-001
    val category: String,
    val title: String,
    val description: String,
    val regulationReference: String? = null,
    val mabhasNumber: String? = null,
    val isMandatory: Boolean = true,
    val isGasRelated: Boolean = false, // فقط با انتخاب ناظر وارد گزارش شود
    val isCustom: Boolean = false, // بند اختصاصی ناظر
    val source: String = "official"
)
