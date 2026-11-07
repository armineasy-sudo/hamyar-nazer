package com.example.service

import com.example.data.ChecklistEntity
import com.example.data.ProjectEntity

/**
 * تولید پیش‌نویس محلی گزارش؛ این کلاس تأییدیه یا گزارش رسمی صادر نمی‌کند.
 * فقط اطلاعاتی را منعکس می‌کند که در چک‌لیست ثبت شده‌اند.
 */
object ReportGenerator {
    enum class ReportType { INITIAL, STAGE, NON_CONFORMANCE, FOLLOW_UP, FINAL, INCOMPLETE }

    fun generate(
        project: ProjectEntity,
        checklists: List<ChecklistEntity>,
        reportType: ReportType = ReportType.STAGE,
        includeRegulations: Boolean = false,
        includeAdminHeader: Boolean = false,
        engineerName: String = "مهندس ناظر"
    ): String {
        val typeText = when (reportType) {
            ReportType.INITIAL -> "بازدید اولیه"
            ReportType.STAGE -> "بازدید مرحله‌ای"
            ReportType.NON_CONFORMANCE -> "بازدید ثبت مغایرت"
            ReportType.FOLLOW_UP -> "بازدید پیگیری"
            ReportType.FINAL -> "بازدید نهایی"
            ReportType.INCOMPLETE -> "بازدید با اطلاعات ناقص"
        }
        val intro = "پیرو ${typeText} از پروژه «${project.name}» واقع در ${project.city}، منطقه ${project.district}، موارد زیر بر اساس اطلاعات ثبت‌شده در چک‌لیست ارائه می‌شود:"
        return intro + "\n\n" + buildBody(checklists, includeRegulations)
    }

    private fun buildBody(items: List<ChecklistEntity>, includeRegulations: Boolean): String {
        val approved = items.filter { it.status in setOf("COMPLETED", "APPROVED", "CONFIRMED") }
        val corrections = items.filter { it.status in setOf("REQUIRES_CORRECTION", "NOT_APPROVED", "REJECTED", "FAILED") }
        val notChecked = items.filter { it.status in setOf("NOT_COMPLETED", "UNCHECKED") }\n        val cannotInspect = items.filter { it.status == "CANNOT_INSPECT" }
        val notApplicable = items.filter { it.status == "NOT_APPLICABLE" }
        val notInStage = items.filter { it.status == "NOT_IN_THIS_STAGE" }

        val output = StringBuilder()
        appendSection(output, "موارد تأییدشده", approved, includeRegulations, false)
        appendSection(output, "موارد نیازمند اصلاح یا دارای مغایرت", corrections, includeRegulations, true)
        appendSection(output, "موارد هنوز بررسی‌نشده", notChecked, includeRegulations, true)\n        appendSection(output, "مواردی که امکان بررسی آن‌ها وجود نداشته است", cannotInspect, includeRegulations, true)
        appendSection(output, "موارد نامرتبط با این پروژه", notApplicable, includeRegulations, true)
        appendSection(output, "مواردی که در این مرحله هنوز اجرا نشده‌اند", notInStage, includeRegulations, true)
        if (output.isEmpty()) output.append("برای این بازدید نتیجه‌ای در چک‌لیست ثبت نشده است.")
        return output.toString().trim()
    }

    private fun appendSection(
        output: StringBuilder,
        title: String,
        items: List<ChecklistEntity>,
        includeRegulations: Boolean,
        includeNotes: Boolean
    ) {
        if (items.isEmpty()) return
        output.appendLine("${title}:")
        items.forEach { item ->
            output.appendLine("• ${item.title}")
            if (includeNotes && item.inspectorNotes.isNotBlank()) {
                output.appendLine("  توضیح ناظر: ${item.inspectorNotes}")
            }
            if (includeRegulations && item.regulationReference.isNotBlank()) {
                output.appendLine("  مرجع ثبت‌شده: ${item.regulationReference}")
            }
        }
        output.appendLine()
    }

    fun generateForCard(
        project: ProjectEntity,
        checklists: List<ChecklistEntity>,
        includeRegulations: Boolean = false
    ): String = generate(project, checklists, ReportType.STAGE, includeRegulations)
}
