package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val city: String,
    val district: String,
    val buildingGroup: String, // الف، ب، ج، د
    val buildingType: String, // مسکونی، تجاری، اداری، صنعتی
    val floorsCount: Int,
    val totalArea: Double,
    val mechanicalSystemType: String, // اسپلیت، چیلر، فن‌کویل، کولر آبی و رادیاتور
    val status: String, // ACTIVE, ARCHIVED
    val createdAt: Long = System.currentTimeMillis()
)
