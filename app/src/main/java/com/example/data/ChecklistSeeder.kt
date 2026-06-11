package com.example.data

object ChecklistSeeder {
    fun getDefaultChecklists(projectId: Long): List<ChecklistEntity> {
        return listOf(
            // BEFORE_CONSTRUCTION
            ChecklistEntity(
                projectId = projectId,
                category = "BEFORE_CONSTRUCTION",
                title = "بررسی تطابق نقشه‌های مکانیکی مصوب با معماری و سازه ساختمان",
                status = "NOT_COMPLETED",
                priority = "HIGH",
                regulationReference = "مبحث ۲ (نظامات اداری) و مبحث ۱۴"
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
                regulationReference = "مبحث ۱۴ (تاسیسات مکانیکی)"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "BEFORE_CONSTRUCTION",
                title = "بررسی مسیر، ابعاد و استانداردهای مصالح دودکش‌ها و انطباق با مقررات ملی",
                status = "NOT_COMPLETED",
                priority = "CRITICAL",
                regulationReference = "مبحث ۱۷ (لوله‌کشی گاز طبیعی)"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "BEFORE_CONSTRUCTION",
                title = "بررسی نقشه‌های تفصیلی سیستم هیدرانت و خودکار اطفاء حریق و تاییدیه آتش‌نشانی",
                status = "NOT_COMPLETED",
                priority = "HIGH",
                regulationReference = "مبحث ۳ (حفاظت در برابر حریق)"
            ),

            // DURING_CONSTRUCTION
            ChecklistEntity(
                projectId = projectId,
                category = "DURING_CONSTRUCTION",
                title = "کنترل شیب‌بندی صحیح، سایزینگ و تراز لوله‌کشی‌های فاضلاب و آب باران قبل از پوشش تفصیلی",
                status = "NOT_COMPLETED",
                priority = "HIGH",
                regulationReference = "مبحث ۱۶ (تاسیسات بهداشتی)"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "DURING_CONSTRUCTION",
                title = "بررسی نصب صحیح ساپورت‌ها، لرزه‌گیرها و بست‌های مهار لوله‌کشی هیدرونیک و بهداشتی",
                status = "NOT_COMPLETED",
                priority = "MEDIUM",
                regulationReference = "مبحث ۱۶ و ۱۴"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "DURING_CONSTRUCTION",
                title = "کنترل عایق‌کاری حرارتی لوله‌های آب گرم رفت و برگشت و کانال‌های سرمایش/گرمایش",
                status = "NOT_COMPLETED",
                priority = "MEDIUM",
                regulationReference = "مبحث ۱۹ (صرفه‌جویی در مصرف انرژی)"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "DURING_CONSTRUCTION",
                title = "بررسی نصب صحیح شیرهای خودکار، زون‌بندی و کلکتورهای اصلی اطفاء حریق",
                status = "NOT_COMPLETED",
                priority = "HIGH",
                regulationReference = "مبحث ۳ (حفاظت در برابر حریق)"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "DURING_CONSTRUCTION",
                title = "کنترل عدم تداخل لوله‌کشی گاز با کابل‌های برق و داکت‌های هواکش",
                status = "NOT_COMPLETED",
                priority = "CRITICAL",
                regulationReference = "مبحث ۱۷ (لوله‌کشی گاز طبیعی)"
            ),

            // BEFORE_FINAL_APPROVAL
            ChecklistEntity(
                projectId = projectId,
                category = "BEFORE_FINAL_APPROVAL",
                title = "نظارت بر تست آب‌بندی فاضلاب بر اساس گرانش به مدت ۱۵ دقیقه ممتد",
                status = "NOT_COMPLETED",
                priority = "HIGH",
                regulationReference = "مبحث ۱۶ (تاسیسات بهداشتی)"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "BEFORE_FINAL_APPROVAL",
                title = "نظارت بر تست فشار هیدرواستاتیک لوله‌کشی آب (حداقل ۱۰ بار به مدت ۲ ساعت)",
                status = "NOT_COMPLETED",
                priority = "HIGH",
                regulationReference = "مبحث ۱۶ (تاسیسات بهداشتی)"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "BEFORE_FINAL_APPROVAL",
                title = "نظارت بر تست نشت دودکش‌ها با روش مشعل روشن و تضمین مکش ایده آل هوا",
                status = "NOT_COMPLETED",
                priority = "CRITICAL",
                regulationReference = "مبحث ۱۷ (لوله‌کشی گاز طبیعی)"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "BEFORE_FINAL_APPROVAL",
                title = "تست عملکرد دیگ موتورخانه، مشعل، چیلر جذبی/تراکمی و پمپ‌های کلکتور جریان حرارتی",
                status = "NOT_COMPLETED",
                priority = "MEDIUM",
                regulationReference = "مبحث ۱۴ (تاسیسات مکانیکی)"
            ),
            ChecklistEntity(
                projectId = projectId,
                category = "BEFORE_FINAL_APPROVAL",
                title = "کنترل نهایی شیرهای تخلیه فشار هوا (ایرونت)، شیر اطمینان موتورخانه و سوویچ‌های ترموستاتیک",
                status = "NOT_COMPLETED",
                priority = "CRITICAL",
                regulationReference = "مبحث ۱۴ (تاسیسات مکانیکی)"
            )
        )
    }
}
