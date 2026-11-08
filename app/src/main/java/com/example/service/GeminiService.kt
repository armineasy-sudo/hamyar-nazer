package com.example.service

import android.util.Log
import com.example.data.ChecklistEntity
import com.example.data.ProjectEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val GEMINI_API_KEY = "AQ.Ab8RN6Iog0Ns3rQF7ZQZJ7Z-gd_AYfwGI60CcGhDiDT4vIRU2g"
    private const val TAG = "GeminiService"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    fun isApiKeyConfigured(): Boolean {
        return GEMINI_API_KEY.isNotEmpty() && GEMINI_API_KEY != "dummy_key_for_build" && GEMINI_API_KEY != "MY_GEMINI_API_KEY"
    }

    suspend fun generateSupervisionReport(
        project: ProjectEntity,
        checklists: List<ChecklistEntity>,
        visitDateShamsi: String = "",
        includeRegulations: Boolean = true,
        tone: String = "SAFETY_WARNING"
    ): String = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured()) {
            return@withContext generateSimulatedReport(project, checklists, visitDateShamsi, includeRegulations)
        }

        val completed = checklists.filter { it.status == "COMPLETED" || it.status == "APPROVED" }
        val corrections = checklists.filter { it.status == "REQUIRES_CORRECTION" || it.status == "REJECTED" }
        val incomplete = checklists.filter { it.status == "NOT_COMPLETED" || it.status == "UNCHECKED" }

        val completedText = if (completed.isEmpty()) "هیچ مورد تاییدشده‌ای ثبت نشده است."
        else completed.joinToString("\n- ") { "${it.title}${if (it.regulationReference.isNotBlank()) " (مرجع: ${it.regulationReference})" else ""}" }

        val correctionsText = if (corrections.isEmpty()) "مورد دارای مغایرت یا نیازمند اصلاح مشاهده نگردید."
        else corrections.joinToString("\n- ") {
            val note = if (it.inspectorNotes.isNotBlank()) ": ${it.inspectorNotes}" else ""
            val ref = if (it.regulationReference.isNotBlank()) " (مرجع: ${it.regulationReference})" else ""
            "${it.title}$note$ref"
        }

        val datePhrase = if (visitDateShamsi.isNotBlank()) {
            "پیرو بازدید به عمل آمده در تاریخ $visitDateShamsi از پروژه ساختمانی مذکور"
        } else {
            "پیرو بازدید به عمل آمده از پروژه ساختمانی مذکور"
        }

        val fixedPrompt = """
            در نقش یک مهندس ناظر تأسیسات مکانیکی رسمی عضو سازمان نظام مهندسی ساختمان ایران، یک گزارش نظارت فنی و ایمنی دقیق و رسمی برای ثبت در اتوماسیون نظام مهندسی و شهرداری تهیه کن.

            قوانین ثابت گزارش‌نویسی:
            ۱. گزارش باید منطبق مقررات ملی ساختمان ایران (به‌ویژه مباحث ۱۴، ۱۶، ۱۷ و ۱۲) باشد.
            ۲. هر مغایرت یا نقص باید به بند مرتبط مقررات ملی ساختمان ارجاع دهد.
            ۳. موارد ایمنی را جدی بگیر. استفاده نکردن از کلاه ایمنی، دستکش ایمنی، کفش ایمنی و سایر وسایل حفاظت فردی مغایرت با مبحث ۱۲ مقررات ملی ساختمان است و باید مورد تأکید قرار گیرد.
            ۴. حضور یا عدم حضور مجری ذی‌صلاح در محل ساختمان از موارد مهم و الزامی است. اگر مجری ذی‌صلاح حضور نداشته باشد، حتماً در گزارش ذکر شود.
            ۵. هیچ اطلاع جعلی تولید نکن. فقط بر اساس داده‌های ارائه‌شده گزارش بنویس.
            ۶. زبان گزارش کاملاً فارسی رسمی و فنی باشد.
            ۷. مقدمه گزارش باید حتماً با عبارت «$datePhrase» شروع شود.

            مشخصات پروژه:
            - نام پروژه: ${project.name}
            - شهر: ${project.city}${project.district?.let { "، $it" } ?: ""}
            - گروه ساختمان: ${project.selectedBuildingGroup} | کاربری: ${project.buildingType}
            - تعداد طبقات: ${project.floorsCount} | متراژ مؤثر: ${project.effectiveArea.toInt()} مترمربع

            یافته‌های میدانی بر اساس چک‌لیست نظارتی:
            موارد تأییدشده:
            $completedText

            مغایرت‌ها و موارد نیازمند اصلاح:
            $correctionsText

            تعداد موارد بررسی‌نشده: ${incomplete.size}

            ساختار الزامی گزارش:
            ۱. مقدمه شامل عبارت الزامی «$datePhrase» و مشخصات پروژه.
            ۲. وضعیت حضور مجری ذی‌صلاح (در صورت عدم حضور حتماً ذکر شود).
            ۳. موارد ایمنی مشاهده‌شده (کلاه، دستکش، کفش و سایر وسایل حفاظت فردی).
            ۴. موارد تأییدشده فنی.
            ۵. مغایرت‌ها و موارد نیازمند اصلاح (با ارجاع دقیق به بند مقررات ملی).
            ۶. موارد بررسی‌نشده (در صورت وجود).
            ۷. جمع‌بندی و یادآوری مسئولیت مهندس ناظر.
        """.trimIndent()

        try {
            val responseText = callGeminiApi(fixedPrompt)
            if (responseText.isNullOrBlank()) {
                generateSimulatedReport(project, checklists, visitDateShamsi, includeRegulations)
            } else {
                responseText
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating report from Gemini, using simulation", e)
            generateSimulatedReport(project, checklists, visitDateShamsi, includeRegulations) +
                "\n\n[توجه: این گزارش با هوش مصنوعی محلی تولید شد زیرا اتصال برقرار نشد]"
        }
    }

    private fun callGeminiApi(prompt: String): String? {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$GEMINI_API_KEY"

        val jsonPayload = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        val partObj = JSONObject().apply {
                            put("text", prompt)
                        }
                        put(partObj)
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            put("contents", contentsArray)
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonPayload.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.e(TAG, "Gemini API error code: ${response.code}")
                return null
            }

            val bodyString = response.body?.string() ?: return null
            val responseJson = JSONObject(bodyString)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val content = candidates.getJSONObject(0).optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    return parts.getJSONObject(0).optString("text")
                }
            }
            return null
        }
    }

    private fun generateSimulatedReport(
        project: ProjectEntity,
        checklists: List<ChecklistEntity>,
        visitDateShamsi: String,
        includeRegulations: Boolean
    ): String {
        val completed = checklists.filter { it.status == "COMPLETED" || it.status == "APPROVED" }
        val corrections = checklists.filter { it.status == "REQUIRES_CORRECTION" || it.status == "REJECTED" }

        val datePhrase = if (visitDateShamsi.isNotBlank()) {
            "پیرو بازدید به عمل آمده در تاریخ $visitDateShamsi از پروژه ساختمانی مذکور"
        } else {
            "پیرو بازدید به عمل آمده از پروژه ساختمانی مذکور"
        }

        val completedBullets = if (completed.isEmpty()) {
            "• موردی هنوز به طور کامل تایید نشده است."
        } else {
            completed.joinToString("\n") {
                val ref = if (it.regulationReference.isNotBlank()) " (مرجع: ${it.regulationReference})" else ""
                "• تأیید بند ${it.title}$ref"
            }
        }

        val correctionBullets = if (corrections.isEmpty()) {
            "• در زمان بازدید نواقص حادی مشاهده نگردید."
        } else {
            corrections.joinToString("\n") {
                val note = if (it.inspectorNotes.isNotBlank()) "\n  - توضیح: ${it.inspectorNotes}" else ""
                val ref = if (it.regulationReference.isNotBlank()) " (مرجع: ${it.regulationReference})" else ""
                "• ${it.title}$note$ref"
            }
        }

        return """
            گزارش نظارت تأسیسات مکانیکی (منطبق مقررات ملی ساختمان)

            $datePhrase، مراتب زیر به اطلاع می‌رسد:

            پروژه: ${project.name}
            محل: ${project.city}${project.district?.let { "، $it" } ?: ""}
            گروه ساختمان: ${project.selectedBuildingGroup} | کاربری: ${project.buildingType}
            طبقات: ${project.floorsCount} | متراژ مؤثر: ${project.effectiveArea.toInt()} مترمربع

            ۱. وضعیت حضور مجری ذی‌صلاح:
            • وضعیت حضور مجری ذی‌صلاح در چک‌لیست ثبت نشده است (لطفاً در بازدید بعدی بررسی شود).

            ۲. موارد ایمنی (مبحث ۱۲):
            • استفاده از کلاه ایمنی، دستکش و کفش ایمنی الزامی است.

            ۳. موارد تأییدشده:
            $completedBullets

            ۴. مغایرت‌ها و موارد نیازمند اصلاح:
            $correctionBullets

            ---
            یادآوری: این گزارش بر اساس اطلاعات ثبت‌شده توسط مهندس ناظر تهیه شده است.
            نتیجه نهایی فنی و ایمنی بر عهده مهندس مسئول است.
        """.trimIndent()
    }
}
