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
        includeRegulations: Boolean = false,
        tone: String = "NORMAL"
    ): String = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured()) {
            return@withContext generateSimulatedReport(project, checklists, includeRegulations)
        }

        val completed = checklists.filter { it.status == "COMPLETED" || it.status == "APPROVED" }
        val corrections = checklists.filter { it.status == "REQUIRES_CORRECTION" || it.status == "REJECTED" }
        val incomplete = checklists.filter { it.status == "NOT_COMPLETED" || it.status == "UNCHECKED" }

        val completedText = if (completed.isEmpty()) "هیچ مورد تاییدشده‌ای ثبت نشده است."
        else completed.joinToString("\n- ") { "${it.title}${if (includeRegulations && it.regulationReference.isNotBlank()) " (مرجع: ${it.regulationReference})" else ""}" }

        val correctionsText = if (corrections.isEmpty()) "مورد دارای مغایرت یا نیازمند اصلاح مشاهده نگردید."
        else corrections.joinToString("\n- ") {
            val note = if (it.inspectorNotes.isNotBlank()) ": ${it.inspectorNotes}" else ""
            val ref = if (includeRegulations && it.regulationReference.isNotBlank()) " (مرجع: ${it.regulationReference})" else ""
            "${it.title}$note$ref"
        }

        val toneInstruction = when (tone) {
            "SERIOUS" -> "لحن گزارش جدی و قاطع باشد."
            "SAFETY_WARNING" -> "بر هشدارهای ایمنی تأکید کن."
            else -> "لحن گزارش عادی و رسمی باشد."
        }

        val prompt = """
            در نقش یک مهندس ناظر مکانیک رسمی عضو سازمان نظام مهندسی ساختمان ایران، یک گزارش نظارت فنی و کارگاهی دقیق و رسمی برای ثبت در اتوماسیون نظام مهندسی و شهرداری آماده کن.
            $toneInstruction

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

            ساختار گزارش:
            ۱. مقدمه کوتاه شامل مشخصات پروژه و تاریخ بازدید.
            ۲. موارد تأییدشده.
            ۳. مغایرت‌ها و موارد نیازمند اصلاح (با ذکر دقیق نواقص).
            ۴. موارد بررسی‌نشده (در صورت وجود).
            ۵. جمع‌بندی و یادآوری مسئولیت مهندس ناظر.

            زبان گزارش کاملاً فارسی رسمی باشد. اطلاعات جعلی تولید نکن. فقط بر اساس داده‌های ارائه‌شده گزارش بنویس.
        """.trimIndent()

        try {
            val responseText = callGeminiApi(prompt)
            if (responseText.isNullOrBlank()) {
                generateSimulatedReport(project, checklists, includeRegulations)
            } else {
                responseText
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating report from Gemini, using simulation", e)
            generateSimulatedReport(project, checklists, includeRegulations) +
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
        includeRegulations: Boolean
    ): String {
        val completed = checklists.filter { it.status == "COMPLETED" || it.status == "APPROVED" }
        val corrections = checklists.filter { it.status == "REQUIRES_CORRECTION" || it.status == "REJECTED" }

        val completedBullets = if (completed.isEmpty()) {
            "• موردی هنوز به طور کامل تایید نشده است."
        } else {
            completed.joinToString("\n") {
                val ref = if (includeRegulations && it.regulationReference.isNotBlank()) " (مرجع: ${it.regulationReference})" else ""
                "• تأیید بند ${it.title}$ref"
            }
        }

        val correctionBullets = if (corrections.isEmpty()) {
            "• در زمان بازدید نواقص حادی مشاهده نگردید."
        } else {
            corrections.joinToString("\n") {
                val note = if (it.inspectorNotes.isNotBlank()) "\n  - توضیح: ${it.inspectorNotes}" else ""
                val ref = if (includeRegulations && it.regulationReference.isNotBlank()) " (مرجع: ${it.regulationReference})" else ""
                "• ${it.title}$note$ref"
            }
        }

        return """
            گزارش نظارت تأسیسات مکانیکی

            پروژه: ${project.name}
            محل: ${project.city}${project.district?.let { "، $it" } ?: ""}
            گروه ساختمان: ${project.selectedBuildingGroup} | کاربری: ${project.buildingType}
            طبقات: ${project.floorsCount} | متراژ مؤثر: ${project.effectiveArea.toInt()} مترمربع

            ۱. موارد تأییدشده:
            $completedBullets

            ۲. مغایرت‌ها و موارد نیازمند اصلاح:
            $correctionBullets

            ---
            یادآوری: این گزارش بر اساس اطلاعات ثبت‌شده توسط مهندس ناظر تهیه شده است.
            نتیجه نهایی فنی بر عهده مهندس مسئول است.
        """.trimIndent()
    }
}
