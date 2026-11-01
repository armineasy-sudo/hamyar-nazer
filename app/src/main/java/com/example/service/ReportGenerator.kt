package com.example.service

import com.example.data.ChecklistEntity
import com.example.data.ProjectEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * تولیدکننده گزارش محلی (آفلاین) برای همیار ناظر.
 * مطابق الزامات نسخه اول:
 * - مقدمه کوتاه + بدنه
 * - بدون سربرگ خودکار (شهرداری، نظام مهندسی، شماره پرونده و...) مگر کاربر بخواهد
 * - گزینه روشن/خاموش کردن ارجاع به مقررات
 * - تمایز موارد تأییدشده، مغایرت، نیازمند اصلاح و بررسی‌نشده
 * - عدم اختراع اطلاعات
 */
object ReportGenerator {

    enum class ReportType {
        INITIAL,           // بازدید اولیه
        STAGE,             // مرحله‌ای
        NON_CONFORMANCE,   // مغایرت
        FOLLOW_UP,         // پیگیری رفع مغایرت
        FINAL,             // نهایی
        INCOMPLETE         // ناقص / نیازمند بررسی تکمیلی
    }

    fun generate(
        project: ProjectEntity,
        checklists: List<ChecklistEntity>,
        reportType: ReportType = ReportType.STAGE,
        includeRegulations: Boolean = false,
        includeAdminHeader: Boolean = false,
        engineerName: String = "مهندس ناظر"
    ): String {
        val now = SimpleDateFormat("yyyy/MM/dd", Locale("fa", "IR")).format(Date())

        val approved = checklists.filter { it.status == "COMPLETED" || it.status == "APPROVED" }
        val needsCorrection = checklists.filter { it.status == "REQUIRES_CORRECTION" }
        val rejected = checklists.filter { it.status == "REJECTED" || it.status == "FAILED" }
        val unchecked = checklists.filter { it.status == "NOT_COMPLETED" || it.status == "UNCHECKED" }
        val notApplicable = checklists.filter { it.status == "NOT_APPLICABLE" }

        val intro = buildIntro(project, reportType, now)
        val body = buildBody(
            approved = approved,
            needsCorrection = needsCorrection,
            rejected = rejected,
            unchecked = unchecked,
            notApplicable = notApplicable,
            includeRegulations = includeRegulations
        )

        val header = if (includeAdminHeader) {
            """
            به: شهرداری محترم منطقه ${project.district} / سازمان نظام مهندسی ساختمان
            موضوع: گزارش نظارت تأسیسات مکانیکی - ${project.name}
            
            """.trimIndent() + "\n\n"
        } else ""

        val disclaimer = """
            
            ---
            یادآوری: این گزارش بر اساس اطلاعات ثبت‌شده توسط مهندس ناظر تهیه شده است. 
            نتیجه نهایی فنی، ارزیابی انطباق و تأیید ایمنی بر عهده مهندس مسئول است. 
            ابزارهای محاسباتی و پیشنهادهای هوش مصنوعی جایگزین قضاوت حرفه‌ای نیستند.
        """.trimIndent()

        return header + intro + "\n\n" + body + disclaimer
    }

    private fun buildIntro(project: ProjectEntity, type: ReportType, date: String): String {
        val typeText = when (type) {
            ReportType.INITIAL -> "بازدید اولیه"
            ReportType.STAGE -> "بازدید مرحله‌ای"
            ReportType.NON_CONFORMANCE -> "بازدید ثبت مغایرت"
            ReportType.FOLLOW_UP -> "بازدید پیگیری رفع مغایرت"
            ReportType.FINAL -> "بازدید نهایی"
            ReportType.INCOMPLETE -> "بازدید با اطلاعات ناقص"
        }

        return """
            در تاریخ \( date از پروژه « \){project.name}» واقع در ${project.city}، منطقه ${project.district}، 
            با کاربری ${project.buildingType}، گروه ساختمانی ${project.buildingGroup}، 
            تعداد طبقات \( {project.floorsCount} و سیستم تأسیساتی « \){project.mechanicalSystemType}» 
            بازدید $typeText به عمل آمد. موارد زیر بر اساس مشاهدات میدانی و اطلاعات ثبت‌شده گزارش می‌شود.
        """.trimIndent()
    }

    private fun buildBody(
        approved: List<ChecklistEntity>,
        needsCorrection: List<ChecklistEntity>,
        rejected: List<ChecklistEntity>,
        unchecked: List<ChecklistEntity>,
        notApplicable: List<ChecklistEntity>,
        includeRegulations: Boolean
    ): String {
        val sb = StringBuilder()

        if (approved.isNotEmpty()) {
            sb.appendLine("۱. موارد تأییدشده:")
            approved.forEach { item ->
                sb.appendLine("- ${item.title}")
                if (includeRegulations && item.regulationReference.isNotBlank()) {
                    sb.appendLine("  (مرجع: ${item.regulationReference})")
                }
            }
            sb.appendLine()
        }

        val discrepancies = needsCorrection + rejected
        if (discrepancies.isNotEmpty()) {
            sb.appendLine("۲. مغایرت‌ها و موارد نیازمند اصلاح:")
            discrepancies.forEach { item ->
                sb.appendLine("- ${item.title}")
                if (item.inspectorNotes.isNotBlank()) {
                    sb.appendLine("  توضیح مهندس: ${item.inspectorNotes}")
                }
                if (item.priority.isNotBlank()) {
                    sb.appendLine("  اولویت: ${item.priority}")
                }
                if (includeRegulations && item.regulationReference.isNotBlank()) {
                    sb.appendLine("  (مرجع: ${item.regulationReference})")
                }
            }
            sb.appendLine()
        }

        if (unchecked.isNotEmpty()) {
            sb.appendLine("۳. موارد بررسی‌نشده:")
            unchecked.forEach { item ->
                sb.appendLine("- ${item.title}")
            }
            sb.appendLine()
        }

        if (notApplicable.isNotEmpty()) {
            sb.appendLine("۴. موارد غیرقابل اعمال در این پروژه:")
            notApplicable.forEach { item ->
                sb.appendLine("- ${item.title}")
            }
            sb.appendLine()
        }

        if (sb.isEmpty()) {
            sb.appendLine("هیچ موردی در چک‌لیست ثبت نشده است. اطلاعات بازدید ناقص است.")
        }

        return sb.toString().trim()
    }

    /** نسخه ساده برای کپی سریع در کارت گزارش */
    fun generateForCard(
        project: ProjectEntity,
        checklists: List<ChecklistEntity>,
        includeRegulations: Boolean = false
    ): String = generate(
        project = project,
        checklists = checklists,
        reportType = ReportType.STAGE,
        includeRegulations = includeRegulations,
        includeAdminHeader = false
    )
}
