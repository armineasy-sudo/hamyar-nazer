package com.example.data

object SampleDataSeeder {

    fun getSampleProjects(): List<ProjectEntity> = listOf(
        ProjectEntity(
            name = "پروژه مهندس آرمین",
            ownerName = "مهندس آرمین ایزی",
            city = "کرج",
            district = "کردان",
            buildingType = "مسکونی",
            floorsCount = 4,
            effectiveArea = 850.0,
            suggestedBuildingGroup = "ب",
            selectedBuildingGroup = "ب",
            groupChangedByUser = false,
            notes = "پروژه نمونه در کردان",
            status = "ACTIVE"
        ),
        ProjectEntity(
            name = "پروژه آقای مهندس صمیمی",
            ownerName = "مهندس صمیمی",
            city = "کرج",
            district = "عظیمیه",
            buildingType = "مسکونی",
            floorsCount = 6,
            effectiveArea = 2100.0,
            suggestedBuildingGroup = "ج",
            selectedBuildingGroup = "ج",
            groupChangedByUser = false,
            notes = "پروژه نمونه در عظیمیه",
            status = "ACTIVE"
        ),
        ProjectEntity(
            name = "پروژه مسکونی پارس",
            ownerName = "شرکت پارس ساخت",
            city = "کرج",
            district = "کیانمهر",
            buildingType = "مسکونی",
            floorsCount = 5,
            effectiveArea = 1600.0,
            suggestedBuildingGroup = "ب",
            selectedBuildingGroup = "ب",
            groupChangedByUser = false,
            notes = "پروژه نمونه در کیانمهر",
            status = "ACTIVE"
        )
    )

    fun getSampleVisits(projectId: Long, projectName: String): List<InspectionVisitEntity> {
        return when {
            projectName.contains("آرمین") -> listOf(
                InspectionVisitEntity(
                    projectId = projectId,
                    visitDateShamsi = "1404/06/10",
                    visitTime = "09:30",
                    executionStage = "لوله‌کشی آب سرد و گرم طبقه همکف",
                    notes = "لوله‌کشی طبقه همکف با رعایت شیب مناسب انجام شد.",
                    status = "COMPLETED"
                ),
                InspectionVisitEntity(
                    projectId = projectId,
                    visitDateShamsi = "1404/07/05",
                    visitTime = "11:00",
                    executionStage = "نصب رادیاتورها و شیرها",
                    notes = "رادیاتورها نصب شده‌اند. آزمایش فشار انجام نشده.",
                    status = "COMPLETED"
                )
            )
            projectName.contains("صمیمی") -> listOf(
                InspectionVisitEntity(
                    projectId = projectId,
                    visitDateShamsi = "1404/05/20",
                    visitTime = "10:15",
                    executionStage = "لوله‌کشی گاز و فاضلاب",
                    notes = "لوله‌کشی گاز مطابق مبحث ۱۷ انجام شد.",
                    status = "COMPLETED"
                ),
                InspectionVisitEntity(
                    projectId = projectId,
                    visitDateShamsi = "1404/08/01",
                    visitTime = "14:30",
                    executionStage = "نصب پکیج حرارتی و بویلر",
                    notes = "پکیج حرارتی نصب شد. نیاز به بررسی دودکش دارد.",
                    status = "COMPLETED"
                )
            )
            else -> listOf(
                InspectionVisitEntity(
                    projectId = projectId,
                    visitDateShamsi = "1404/07/15",
                    visitTime = "08:45",
                    executionStage = "لوله‌کشی آب سرد و گرم طبقات اول و دوم",
                    notes = "لوله‌کشی دو طبقه انجام شد. شیب مناسب رعایت شده.",
                    status = "COMPLETED"
                )
            )
        }
    }
}
