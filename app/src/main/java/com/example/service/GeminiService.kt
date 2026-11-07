package com.example.service

import com.example.data.ChecklistEntity
import com.example.data.ProjectEntity

/**
 * در نسخه اول هیچ کلید یا فراخوانی مستقیم سرویس هوش مصنوعی در APK وجود ندارد.
 * اتصال AI فقط پس از پیاده‌سازی Backend امن فعال خواهد شد.
 */
object GeminiService {
    fun isApiKeyConfigured(): Boolean = false

    suspend fun generateSupervisionReport(
        project: ProjectEntity,
        checklists: List<ChecklistEntity>
    ): String = ReportGenerator.generateForCard(
        project = project,
        checklists = checklists,
        includeRegulations = false
    )

    suspend fun analyzeMechanicalDrawing(
        drawingType: String,
        simulatedDetails: String
    ): String = "بررسی نقشه در نسخه اول فعال نیست و در نسخه‌های بعدی ارائه خواهد شد."
}
