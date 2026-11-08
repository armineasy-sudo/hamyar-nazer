package com.example.data

object ChecklistSeeder {
    fun getDefaultChecklists(projectId: Long): List<ChecklistEntity> {
        return listOf(
            // موارد اولیه و ایمنی (مبحث ۱۲ و حضور مجری)
            ChecklistEntity(
                projectId = projectId,
                category = "SAFETY",
                title = "حضور مجری ذی‌صلاح در محل ساختمان",
                status = "NOT_COMPLETED",
                priority = "CRITICAL",
                regulationReference = "مبحث ۲ مقررات ملی ساختمان (نظامات اداری)"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "SAFETY",
                title = "استفاده از کلاه ایمنی توسط کارگران و ناظر",
                status = "NOT_COMPLETED",
                priority = "CRITICAL",
                regulationReference = "مبحث ۱۲ مقررات ملی ساختمان (ایمنی و حفاظت کار)"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "SAFETY",
                title = "استفاده از دستکش ایمنی مناسب با نوع کار",
                status = "NOT_COMPLETED",
                priority = "CRITICAL",
                regulationReference = "مبحث ۱۲ مقررات ملی ساختمان"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "SAFETY",
                title = "استفاده از کفش ایمنی مناسب",
                status = "NOT_COMPLETED",
                priority = "HIGH",
                regulationReference = "مبحث ۱۲ مقررات ملی ساختمان"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "SAFETY",
                title = "وجود سایر وسایل حفاظت فردی مورد نیاز (جلیقه، ماسک، کمربند ایمنی)",
                status = "NOT_COMPLETED",
                priority = "MEDIUM",
                regulationReference = "مبحث ۱۲ مقررات ملی ساختمان"
            ),

            // BEFORE_CONSTRUCTION
            ChecklistEntity(
                projectId = projectId,
                category = "BEFORE_CONSTRUCTION",
                title = "بررسی تطابق نقشه‌های مکانیکی مصوب با معماری و سازه ساختمان",
                status = "NOT_COMPLETED",
                priority = "HIGH",
                regulationReference = "مبحث ۲ و مبحث ۱۴"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "BEFORE_CONSTRUCTION",
                title = "بررسی مساحت، ارتفاع و تهویه مناسب فضای موتورخانه بر اساس ظرفیت تجهیزات",
                status = "NOT_COMPLETED",
                priority = "CRITICAL",
                regulationReference = "مبحث ۱۴ (تاسیسات مکانیکی)"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "BEFORE_CONSTRUCTION",
                title = "تطبیق ظرفیت دیگ، مشعل و مخازن ذخیره با مندرجات نقشه‌های مصوب",
                status = "NOT_COMPLETED",
                priority = "MEDIUM",
                regulationReference = "مبحث ۱۴"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "BEFORE_CONSTRUCTION",
                title = "بررسی مسیر، ابعاد و استانداردهای مصالح دودکش‌ها",
                status = "NOT_COMPLETED",
                priority = "CRITICAL",
                regulationReference = "مبحث ۱۷ و مبحث ۱۴"
            ),

            // DURING_CONSTRUCTION
            ChecklistEntity(
                projectId = projectId,
                category = "DURING_CONSTRUCTION",
                title = "کنترل شیب‌بندی صحیح، سایزینگ و تراز لوله‌کشی‌های فاضلاب و آب باران",
                status = "NOT_COMPLETED",
                priority = "HIGH",
                regulationReference = "مبحث ۱۶ (تاسیسات بهداشتی)"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "DURING_CONSTRUCTION",
                title = "بررسی نصب صحیح ساپورت‌ها و بست‌های مهار لوله‌کشی",
                status = "NOT_COMPLETED",
                priority = "MEDIUM",
                regulationReference = "مبحث ۱۶ و ۱۴"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "DURING_CONSTRUCTION",
                title = "کنترل عایق‌کاری حرارتی لوله‌های آب گرم و کانال‌ها",
                status = "NOT_COMPLETED",
                priority = "MEDIUM",
                regulationReference = "مبحث ۱۹ و مبحث ۱۴"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "DURING_CONSTRUCTION",
                title = "کنترل عدم تداخل لوله‌کشی گاز با کابل‌های برق و داکت‌های هواکش",
                status = "NOT_COMPLETED",
                priority = "CRITICAL",
                regulationReference = "مبحث ۱۷"
            ),

            // BEFORE_FINAL_APPROVAL
            ChecklistEntity(
                projectId = projectId,
                category = "BEFORE_FINAL_APPROVAL",
                title = "نظارت بر تست آب‌بندی فاضلاب",
                status = "NOT_COMPLETED",
                priority = "HIGH",
                regulationReference = "مبحث ۱۶"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "BEFORE_FINAL_APPROVAL",
                title = "نظارت بر تست فشار هیدرواستاتیک لوله‌کشی آب",
                status = "NOT_COMPLETED",
                priority = "HIGH",
                regulationReference = "مبحث ۱۶"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "BEFORE_FINAL_APPROVAL",
                title = "نظارت بر تست نشت دودکش‌ها",
                status = "NOT_COMPLETED",
                priority = "CRITICAL",
                regulationReference = "مبحث ۱۷"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "BEFORE_FINAL_APPROVAL",
                title = "تست عملکرد تجهیزات موتورخانه (دیگ، مشعل، پمپ)",
                status = "NOT_COMPLETED",
                priority = "MEDIUM",
                regulationReference = "مبحث ۱۴"
            )
        )
    }
}
