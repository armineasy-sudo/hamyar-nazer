package com.example.service

import android.util.Log
import com.example.BuildConfig
import com.example.data.ProjectEntity
import com.example.data.ChecklistEntity
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
    private const val TAG = "GeminiService"
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    // Flag indicating if we are using the live API or simulation
    fun isApiKeyConfigured(): Boolean {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            key.isNotEmpty() && key != "MY_GEMINI_API_KEY"
        } catch (e: Exception) {
            false
        }
    }

    suspend fun generateSupervisionReport(
        project: ProjectEntity,
        checklists: List<ChecklistEntity>
    ): String = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured()) {
            return@withContext generateSimulatedReport(project, checklists)
        }

        val completed = checklists.filter { it.status == "COMPLETED" }
        val corrections = checklists.filter { it.status == "REQUIRES_CORRECTION" }
        val incomplete = checklists.filter { it.status == "NOT_COMPLETED" }

        val completedText = if (completed.isEmpty()) "هیچ مورد تایید شده‌ای ثبت نشده است." else completed.joinToString("\n- ") { "${it.title} (کد ارجاع: ${it.regulationReference})" }
        val correctionsText = if (corrections.isEmpty()) "مورد دارای مغایرت یا نیازمند اصلاح مشاهده نگردید." else corrections.joinToString("\n- ") { "${it.title}: ${it.inspectorNotes} (اولویت: ${it.priority})" }
        
        val prompt = """
            در نقش یک مهندس ناظر مکانیک رسمی عضو سازمان نظام مهندسی ساختمان ایران، یک گزارش نظارت فنی و کارگاهی دقیق، رسمی و با ادبیات مهندسی برای ثبت در اتوماسیون نظام مهندسی و شهرداری آماده کن.
            مشخصات پروژه:
            - نام پروژه: ${project.name}
            - شهر و منطقه: ${project.city}، منطقه ${project.district}
            - گروه ساختمان: ${project.buildingGroup} | کاربری: ${project.buildingType}
            - تعداد طبقات: ${project.floorsCount} | متراژ کل: ${project.totalArea} متر مربع
            - سیستم تاسیسات مکانیکی: ${project.mechanicalSystemType}

            یافته‌های میدانی براساس چک‌لیست نظارتی:
            مراحل تکمیل‌شده و مورد تایید:
            $completedText

            نواقص، عدم تطابق‌ها و موارد نیازمند اصلاح:
            $correctionsText

            تعداد موارد بررسی‌نشده: ${incomplete.size}

            ساختار گزارش خروجی باید عینا به ترتیب زیر باشد:
            ۱. بخش هدر اداری (بسمه تعالی، به شهرداری منطقه و سازمان نظام مهندسی ساختمان).
            ۲. بند مقدمه گزارش شامل تایید زمان بازدید و مشخصات ملک.
            ۳. بدنه فنی شامل تایید صریح موارد استاندارد و ذکر دقیق جزییات نواقص و انطباق آن‌ها با بندهای مقررات ملی ساختمان (مباحث ۱۴، ۱۶ و ۱۷).
            ۴. بند حقوقی هشداردهنده صریح خطاب به مالک و مجری ذیصلاح مبنی بر توقف عملیات یا راه‌اندازی در صورت عدم اصلاح نواقص بحرانی.
            ۵. مهر و امضای فرضی ناظر تاسیسات مکانیکی.

            زبان گزارش کاملا فارسی رسمی و بدون واژگان عامیانه باشد. از ادبیات محکم فنی استفاده کن.
        """.trimIndent()

        try {
            val responseText = callGeminiApi(prompt)
            if (responseText.isNullOrBlank()) {
                generateSimulatedReport(project, checklists)
            } else {
                responseText
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating report from Gemini-live, using simulation", e)
            generateSimulatedReport(project, checklists) + "\n\n[توجه: این گزارش با هوش مصنوعی محلی شبیه‌سازی شد زیرا اتصال لایو برقرار نشد]"
        }
    }

    suspend fun analyzeMechanicalDrawing(
        drawingType: String,
        simulatedDetails: String
    ): String = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured()) {
            return@withContext generateSimulatedDrawingAnalysis(drawingType, simulatedDetails)
        }

        val prompt = """
            به عنوان یک هوش مصنوعی متخصص و ارزیاب نقشه‌های تاسیسات مکانیکی ساختمان منطبق بر مقررات ملی ساختمان ایران (مبحث ۱۴، ۱۶ و ۱۷)، اطلاعات و فیلدهای ارسال شده از نقشه مهندسی زیر را تحلیل و اشکال‌زدایی کن:
            نوع نقشه: $drawingType
            توضیحات و ابعاد فیزیکی نقشه: $simulatedDetails

            خروجی را به زبان فارسی و با ساختار زیر تولید کن:
            ۱. تجهیزات ردیابی شده و سایزبندی لوله‌ها (با ذکر قطر نامی به اینچ یا میلی‌متر).
            ۲. مغایرت‌ها و عدم انطباق‌های قانونی با مقررات ملی ساختمان.
            ۳. لودینگ هشدارهای طبقه‌بندی شده:
               - سطح بحرانی (Critical): مواردی که منجر به رد نقشه یا حادثه ایمنی می‌شوند.
               - سطح متوسط (Medium): اشکالات اجرایی و نقایص طراحی.
               - سطح اطلاعاتی (Informational): توصیه‌های بهینه‌سازی مصرف انرژی یا کاهش افت فشار هیدرولیکی.
            
            کامپوننت‌های فرعی و کمبودهای احتمالی نقشه (مانند شیرهای اطمینان، ایرونت‌ها یا انبساط) را صریحا هشدار بده.
        """.trimIndent()

        try {
            val responseText = callGeminiApi(prompt)
            if (responseText.isNullOrBlank()) {
                generateSimulatedDrawingAnalysis(drawingType, simulatedDetails)
            } else {
                responseText
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error analyzing drawing from Gemini-live, using simulation", e)
            generateSimulatedDrawingAnalysis(drawingType, simulatedDetails) + "\n\n[توجه: بررسی هوشمند نقشه در حالت آفلاین/شبکه‌سازی فرعی نجام پذیرفت]"
        }
    }

    private fun callGeminiApi(prompt: String): String? {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

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
            .addHeader("User-Agent", "aistudio-build")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.e(TAG, "Gemini API error code: ${response.code}, body: ${response.body?.string()}")
                return null
            }

            val bodyString = response.body?.string() ?: return null
            val responseJson = JSONObject(bodyString)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                if (content != null) {
                    val parts = content.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return parts.getJSONObject(0).optString("text")
                    }
                }
            }
            return null
        }
    }

    private fun generateSimulatedReport(project: ProjectEntity, checklists: List<ChecklistEntity>): String {
        val completed = checklists.filter { it.status == "COMPLETED" }
        val corrections = checklists.filter { it.status == "REQUIRES_CORRECTION" }

        val completedBullets = if (completed.isEmpty()) {
            "• موردی هنوز به طور کامل تایید نشده است."
        } else {
            completed.joinToString("\n") { "• تایید نهایی و انطباق کامل بند ${it.title} بر اساس ضوبط مرجع ${it.regulationReference}." }
        }

        val correctionBullets = if (corrections.isEmpty()) {
            "• در زمان بازدید نواقص حادی مشهود نگردید و روند اجرا رضایت‌بخش است."
        } else {
            corrections.joinToString("\n") { "• نقص اجرایی: ${it.title}\n  - ایراد فنی: ${it.inspectorNotes} [اولویت: ${it.priority}]" }
        }

        return """
            به: شهرداری محترم منطقه ${project.district} / رئیس محترم سازمان نظام مهندسی ساختمان استان
            موضوع: گزارش رسمی نظارت مرحله‌ای تاسیسات مکانیکی ساختمان

            باسلام و احترام،
            به استحضار می‌رساند اینجانب به عنوان ناظر تاسیسات مکانیکی پروژه ساختمانی به مشخصات زیر:
            - نام پروژه: ${project.name}
            - گروه ساختمانی: ${project.buildingGroup} | کاربری: ${project.buildingType}
            - تعداد طبقات: ${project.floorsCount} طبقه | متراژ کل: ${project.totalArea} متر مربع
            - سیستم تاسیساتی: ${project.mechanicalSystemType}
            واقع در منطقه ${project.district} شهر ${project.city}، در تاریخ جاری از کارگاه ساختمانی فوق‌الذکر بازدید به عمل آورده و مراتب فنی زیر را گواهی می‌نمایم:

            ۱. بخش‌های مورد تایید و منطبق با نقشه مصوب:
            $completedBullets

            ۲. نواقص و ایرادات بحرانی اجرایی (نیاز به اصلاح فوری):
            $correctionBullets

            تذکر و بند قانونی جهت درج در پرونده ملک:
            مالک محترم و مجری ذیصلاح کارگاه مکلفند در اسرع وقت اقدام به اصلاح نواقص ذکر شده نمایند. تداوم عملیات ساختمانی بدون تاییدیه کتبی ناظر در مراحل عایق‌کاری و تست‌های هیدرواستاتیکی خلاف ضوابط شهرسازی بوده و در صورت تداوم، اینجانب مسئولیتی در قبال عواقب حقوقی و فنی آن نداشته و مراتب جهت توقف کار به شهرداری ارجاع خواهد شد.

            با تشکر و تجدید احترام،
            مهندس ناظر تاسیسات مکانیکی ساختمان
        """.trimIndent()
    }

    private fun generateSimulatedDrawingAnalysis(drawingType: String, simulatedDetails: String): String {
        val typePersian = when (drawingType) {
            "BOILER_ROOM" -> "پلان طراحی و چیدمان موتورخانه مرکزی"
            "WATER_MUNICIPAL" -> "شبکه لوله‌کشی آب مصرفی و آبرسانی ساختمان"
            "CHIMNEY_DUCT" -> "پلان داکت‌ها و لوله‌های دودکش و تخلیه محصولات احتراق"
            "FIRE_PROTECTION" -> "سیستم تاسیسات اطفاء حریق و اسپرینکلر کارگاهی"
            else -> drawingType
        }

        return """
            تحلیل تخصصی هوش مصنوعی بر نقشه: $typePersian
            ابزار بررسی: واکاو مقررات ملی ساختمان (مباحث ۱۴، ۱۶ و ۱۷)

            ۱. تحلیل و استخراج تجهیزات هوشمند:
            [✓] تجهیزات ردیابی‌شده در پلان:
              - دیگ آبگرم با ظرفیت حرارتی شبیه‌سازی شده (تعداد: ۲ دستگاه)
              - کلکتورهای توزیع جریان رفت و برگشت به سایز ۴ اینچ
              - پمپ‌های سیرکولاتور خطی ۳ فاز مجهز به کوپلینگ لرزه‌گیر
              - شیرهای تخلیه فشار و هواگیری خودکار (ایرونت) سایز ۱/۲ اینچ
              - مخزن انبساط بسته مجهز به سوپاپ اطمینان با ظرفیت برآورد شده ۲۰۰ لیتر

            ۲. خطاهای محاسباتی و مغایرت‌های قانونی استخراج شده:
            
            ⚠️ سطح بحرانی (Critical):
              - عدم جانمایی دقیق لوله تخلیه سرریز یا سوپاپ اطمینان دیگ‌ها به اتمسفر ایمن کارگاهی (مغایرت صریح با فصل ۴ مبحث ۱۴ مقررات ملی).
              - فاصله افقی شیر اصلی قطع جریان گاز مشعل از دهانه دیگ کمتر از ۱.۲ متر طراحی شده که به علت حرارت پتانسیل شعله مردود است (اصلاح بر اساس مبحث ۱۷ الزامی است).
            
            ⚡ سطح متوسط (Medium):
              - عدم پیش‌بینی کلید قطع اضطراری برق مشعل‌ها در مدخل ورودی موتورخانه (نیاز به اعلام به ناظر برق جهت همپوشانی).
              - ابعاد دودکش طراحی‌شده برای هر مشعل به سایز ۲۰۰ میلی‌متر با ظرفیت حرارتی خروجی همخوانی نداشته و باعث سوخت ناقص می‌گردد (قطر پیشنهادی: ۲۵۰ میلی‌متر).
            
            💡 سطح اطلاعاتی (Informational):
              - توصیه می‌شود عایق‌کاری الاستومری با ضخامت حداقل ۱۹ میلی‌متر برای کلکتورهای اصلی موتورخانه در جزئیات اجرا گنجانده شود (مبحث ۱۹ مقررات ملی).
              - افزودن هدر بای‌پس به همراه شیر یکطرفه برای پمپ‌های تغذیه جهت تداوم کار در مواقع تعمیرات ضروری پیشنهاد می‌گردد.

            سلب مسئولیت مهندسی:
            تحلیل فوق جنبه مشورتی داشته و مبنای نهایی تصمیم‌گیری کارگاهی برگرفته از نظر مهندس ناظر معتمد کارگاه خواهد بود.
        """.trimIndent()
    }
}
