package com.example.data

object SampleDataSeeder {

    fun getSampleProjects(): List<ProjectEntity> = listOf(
        ProjectEntity(
            name = "مجتمع مسکونی پارس سوم",
            ownerName = "شرکت ساختمانی آتین",
            city = "تهران",
            district = "3",
            buildingType = "مسکونی",
            floorsCount = 8,
            effectiveArea = 3200.0,
            suggestedBuildingGroup = "ج",
            selectedBuildingGroup = "ج",
            groupChangedByUser = false,
            notes = "پروژه نمونه با سیستم چیلر و فن‌کویل",
            status = "ACTIVE"
        ),
        ProjectEntity(
            name = "ساختمان اداری نیلوفر",
            ownerName = "آقای محمدی",
            city = "تهران",
            district = "5",
            buildingType = "اداری",
            floorsCount = 5,
            effectiveArea = 1800.0,
            suggestedBuildingGroup = "ب",
            selectedBuildingGroup = "ب",
            groupChangedByUser = false,
            notes = "پروژه نمونه با سیستم اسپلیت",
            status = "ACTIVE"
        ),
        ProjectEntity(
            name = "ویلای شمالی لواسان",
            ownerName = "خانم رضایی",
            city = "تهران",
            district = "1",
            buildingType = "مسکونی",
            floorsCount = 3,
            effectiveArea = 450.0,
            suggestedBuildingGroup = "الف",
            selectedBuildingGroup = "الف",
            groupChangedByUser = false,
            notes = "پروژه نمونه کوچک",
            status = "ACTIVE"
        )
    )
}
