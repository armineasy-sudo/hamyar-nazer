package com.example.data

object ChecklistSeeder {
    /**
     * چک‌لیست بر اساس فایل «چک-لیست-مکانیک.pdf» ارسالی کاربر.
     * متن ردیف‌ها حفظ شده و شماره مبحثی به‌صورت حدسی به آن‌ها اضافه نشده است.
     */
    fun getDefaultChecklists(projectId: Long): List<ChecklistEntity> {
        val rows = listOf(
            "نوع لوله‌های آب سرد و گرم مصرفی (جنس/مدل اجرا را در یادداشت ثبت کنید)" to "BEFORE_CONSTRUCTION",
            "نوع لوله‌های فاضلاب (جنس/مدل اجرا را در یادداشت ثبت کنید)" to "BEFORE_CONSTRUCTION",
            "نوع لوله‌های مصرفی گرمایش (جنس/مدل اجرا را در یادداشت ثبت کنید)" to "BEFORE_CONSTRUCTION",
            "روش لوله‌کشی آب سرد و گرم مصرفی (انشعابی، کلکتوری یا مختلط)" to "BEFORE_CONSTRUCTION",
            "روش لوله‌کشی سیستم گرمایش (انشعابی یا کلکتوری)" to "BEFORE_CONSTRUCTION",
            "لوله‌های فاضلاب و ونت و آب باران دارای کد استاندارد طبق مبحث می‌باشند." to "DURING_CONSTRUCTION",
            "شیب لوله‌های فاضلاب و هواکشی طبق مبحث ۱۶ اجرا شده است." to "DURING_CONSTRUCTION",
            "غلاف‌گذاری لوله‌های فاضلاب انجام شده است." to "DURING_CONSTRUCTION",
            "شیب و تکیه‌گاه لوله‌های افقی و قائم صحیح اجرا شده است." to "DURING_CONSTRUCTION",
            "لوله‌کشی آب باران از فاضلاب مجزا شده است." to "DURING_CONSTRUCTION",
            "ونت‌های لوازم بهداشتی صحیح اجرا شده است." to "DURING_CONSTRUCTION",
            "ونت جهت چاه فاضلاب اجرا شده است؟" to "DURING_CONSTRUCTION",
            "موقعیت چاه فاضلاب طبق نقشه مصوب اجرا شده است؟" to "DURING_CONSTRUCTION",
            "دریچه بازدید طبق نقشه اجرا شده است." to "DURING_CONSTRUCTION",
            "لوله‌کشی فاضلاب مطابق نقشه اجرا شده است." to "DURING_CONSTRUCTION",
            "پیش‌بینی و نصب لوازم بهداشتی مورد نیاز با نوع کاربری متناسب است." to "DURING_CONSTRUCTION",
            "زاویه اتصال ونت به لوله فاضلاب مطابق مبحث ۱۶ اجرا شده است." to "DURING_CONSTRUCTION",
            "تراز لوله ونت افقی حداقل ۱۵ سانتی‌متر بالاتر از سرریز وسیله بهداشتی رعایت شده است." to "DURING_CONSTRUCTION",
            "پیش‌بینی اتصال به فاضلاب شهری انجام شده است." to "DURING_CONSTRUCTION",
            "لوله‌کشی آب سرد و گرم مطابق نقشه مصوب اجرا شده است." to "DURING_CONSTRUCTION",
            "ارتفاع لوله‌های آب سرد و گرم از کف تمام‌شده طبق مبحث اجرا شده است." to "DURING_CONSTRUCTION",
            "سمت راست و چپ بودن آب سرد و گرم رعایت شده است." to "DURING_CONSTRUCTION",
            "فاصله لوازم بهداشتی از جلو و پشت و طرفین رعایت شده است." to "DURING_CONSTRUCTION",
            "عمق یخبندان در لوله‌کشی داخل حیاط رعایت و عایق‌کاری صورت گرفته است." to "DURING_CONSTRUCTION",
            "بست و تکیه‌گاه مناسب جهت جلوگیری از جابه‌جایی در حین نازک‌کاری اجرا شده است." to "DURING_CONSTRUCTION",
            "عمق لوله‌کشی جهت اجرای سایر لوله‌ها و ضخامت کف‌سازی مناسب است." to "DURING_CONSTRUCTION",
            "لوله‌کشی گرمایش سیستم گرمایشی مطابق نقشه مصوب اجرا شده است." to "DURING_CONSTRUCTION",
            "لوله رفت و برگشت رادیاتورها شاقول نصب شده است." to "DURING_CONSTRUCTION",
            "در صورت نصب بخاری اضطراری، فاصله رادیاتور با بخاری رعایت شده است." to "DURING_CONSTRUCTION",
            "ارتفاع لوله‌های رفت و برگشت رادیاتورها از کف تمام‌شده استاندارد است." to "DURING_CONSTRUCTION",
            "لوله‌ها دارای بست و تکیه‌گاه با فاصله مناسب می‌باشند." to "DURING_CONSTRUCTION",
            "عایق‌بندی لوله‌ها صحیح اجرا شده است." to "DURING_CONSTRUCTION",
            "ضخامت ورق کانال طبق مبحث ۱۴ رعایت شده است." to "DURING_CONSTRUCTION",
            "قطعات کانال با فلنج و لرزه‌گیر به یکدیگر متصل شده‌اند." to "DURING_CONSTRUCTION",
            "مسیر و ابعاد کانال، دریچه و اندازه take-off مطابق نقشه مصوب اجرا شده است." to "DURING_CONSTRUCTION",
            "ساپورت زیر کانال‌ها به نحو صحیح اجرا شده است؟" to "DURING_CONSTRUCTION",
            "لوله درین کولر گازی و عدم اتصال به لوله‌کشی فاضلاب صحیح اجرا شده است." to "DURING_CONSTRUCTION",
            "غلاف‌گذاری جهت لوله‌های مسی و کابل کولر گازی صورت گرفته است." to "DURING_CONSTRUCTION",
            "یونیت داخلی کولرگازی در ارتفاع و موقعیت مناسب نصب شده است." to "DURING_CONSTRUCTION",
            "فاصله کولرگازی با ونت و دودکش مطابق مبحث اجرا شده است." to "DURING_CONSTRUCTION",
            "تعویض هوای آشپزخانه برابر نقشه‌های مصوب و مبحث ۱۴ اجرا شده است." to "DURING_CONSTRUCTION",
            "تعویض هوای سرویس‌ها اجرا شده است؟" to "DURING_CONSTRUCTION",
            "تأمین هوای احتراق از خارج طبق مبحث ۱۴ و ۱۷ رعایت شده است." to "DURING_CONSTRUCTION",
            "ممنوعیت نصب وسایل گازسوز در فضاهای داخلی ساختمان طبق ضابطه درج‌شده در چک‌لیست رعایت شده است." to "BEFORE_FINAL_APPROVAL",
            "مسیر دودکش لوازم گازسوز طبق مبحث ۱۷ اجرا شده است." to "BEFORE_FINAL_APPROVAL",
            "لوله دودکش از داخل دیوار و سقف کاذب حمام عبور نکرده است." to "BEFORE_FINAL_APPROVAL",
            "قطر لوله دودکش لوازم گازسوز طبق ضوابط اجرا شده است." to "BEFORE_FINAL_APPROVAL",
            "لوله دودکش در داخل درز انقطاع قرار ندارد." to "BEFORE_FINAL_APPROVAL",
            "هر مصرف‌کننده درون‌سوز دودکش مستقل و مناسب دارد." to "BEFORE_FINAL_APPROVAL",
            "انتهای دودکش‌ها یک متر بالاتر از پشت‌بام و ۳ متر از دیوارهای جانبی فاصله دارد." to "BEFORE_FINAL_APPROVAL",
            "دودکش از استحکام کافی برخوردار است." to "BEFORE_FINAL_APPROVAL",
            "دهانه دودکش بخاری در ارتفاع ۱۲۰ سانتی‌متر از کف نصب شده است." to "BEFORE_FINAL_APPROVAL",
            "دودکش‌ها دارای کلاهک H شکل استاندارد می‌باشند." to "BEFORE_FINAL_APPROVAL",
            "محل اتصالات دودکش‌ها دودبند می‌باشد." to "BEFORE_FINAL_APPROVAL"
        )

        return rows.mapIndexed { index, row ->
            ChecklistEntity(
                projectId = projectId,
                category = row.second,
                title = row.first,
                status = "NOT_COMPLETED",
                priority = "MEDIUM",
                inspectorNotes = if (index < 5) "نوع مصالح یا روش اجرا را اینجا ثبت کنید." else "",
                regulationReference = ""
            )
        }
    }
}
