package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * پروژه ساختمانی
 * تعداد طبقات از روی شالوده محاسبه می‌شود.
 * گروه ساختمان بر اساس مبحث ۲ پیشنهاد می‌شود و قابل ویرایش دستی است.
 */
@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val ownerName: String? = null,
    val address: String? = null,
    val city: String,
    val district: String? = null,
    val buildingType: String,
    val floorsCount: Int,
    val effectiveArea: Double,
    val suggestedBuildingGroup: String,
    val selectedBuildingGroup: String,
    val groupChangedByUser: Boolean = false,
    val notes: String? = null,
    val status: String = "ACTIVE",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
