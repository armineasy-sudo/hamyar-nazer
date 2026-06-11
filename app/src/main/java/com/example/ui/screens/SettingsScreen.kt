package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.GeminiService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val scroll = rememberScrollState()

    // Config options
    var reportLengthPref by remember { mutableStateOf("استاندارد (یک صفحه)") } // استاندارد، خلاصه، توصیفی کامل
    var citationStylePref by remember { mutableStateOf("مقررات ملی کل کشور") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تنظیمات و نقشه راه توسعه ناظر", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "بازگشت", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scroll)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // API Key Status Banner
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (GeminiService.isApiKeyConfigured()) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFF59E0B).copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = if (GeminiService.isApiKeyConfigured()) Color(0xFF10B981) else Color(0xFFF59E0B),
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = if (GeminiService.isApiKeyConfigured()) "اتصال هوش مصنوعی لایو برقرار است" else "هوش مصنوعی آفلاین/شبیه‌سازی شده فعال است",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (GeminiService.isApiKeyConfigured()) 
                                "کلید اختصاصی GEMINI_API_KEY شناسایی شده و گزارش‌ها مستقیماً با مدل Gemini به صورت پویا تولید می‌شوند."
                            else "کلید اختصاصی در پنل Secrets وارد نشده است. برنامه از الگوهای رسمی شبیه‌سازی مهندسی آفلاین استفاده می‌کند تا جریان تایید کارگاه مختل نشود.",
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // User Preferences Configuration Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("تنظیمات تولید گزارش اداری ناظر", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    
                    HorizontalDivider(color = Color.DarkGray)

                    // Length preference
                    Column {
                        Text("طول متن گزارش پیشنهادی:", color = Color.Gray, fontSize = 11.sp)
                        Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            arrayOf("خلاصه (یک بند)", "استاندارد (یک صفحه)", "مفصل فنی").forEach { option ->
                                val selected = reportLengthPref == option
                                Box(
                                    modifier = Modifier
                                        .background(if (selected) Color(0xFF3B82F6) else Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                        .clickable { reportLengthPref = option }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(option, color = if (selected) Color.White else Color.LightGray, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Citation Preference
                    Column {
                        Text("مرجع قوانین و استانداردهای الحاقی:", color = Color.Gray, fontSize = 11.sp)
                        Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            arrayOf("مقررات ملی کل کشور", "پیوست‌های استانی تهران", "ضوابط سازمان آتش‌نشانی").forEach { opt ->
                                val selected = citationStylePref == opt
                                Box(
                                    modifier = Modifier
                                        .background(if (selected) Color(0xFF3B82F6) else Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                        .clickable { citationStylePref = opt }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(opt, color = if (selected) Color.White else Color.LightGray, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Expandable Roadmap Future Header
            Text(
                "نقشه راه توسعه ماژول‌های اینده دستیار ناظر",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Electrical module
            RoadmapItem(
                phase = "ماژول ناظر تاسیسات الکتریکی",
                title = "چک‌لیست تست ارت، تابلو برق و اعلام حریق و سیم‌کشی",
                status = "برنامه‌ریزی شده (نسخه ۱.۵)",
                description = "افزودن تست‌های تخصصی چاه ارت ساختمان بر اساس مبحث ۱۳ مقررات ملی ساختمان، انطباق کابل‌کشی سیستم تهویه با تابلوهای فرعی و مانیتورینگ باتری دتکتورهای گازی.",
                icon = Icons.Default.Star,
                accentColor = Color(0xFFEC4899)
            )

            // Structural module
            RoadmapItem(
                phase = "ماژول ناظر سازه ساختمان",
                title = "ثبت کیورینگ بتن، نتایج آزمایشگاهی و مغایرت تراز آرماتوربندی",
                status = "برنامه‌ریزی شده (نسخه ۲.۰)",
                description = "مستندسازی آزمایش اسلامپ بتن، بررسی ضخامت‌های جوش اسکلت فلزی با هوش مصنوعی تصویری، کنترل ارتعاش دال سقف‌های نوین و هشدارهای لرزه‌ای مبحث ۹ و ۱۰ مقررات ملی.",
                icon = Icons.Default.Star,
                accentColor = Color(0xFF3B82F6)
            )

            // Architectural module
            RoadmapItem(
                phase = "ماژول ناظر معماری و ایمنی",
                title = "انطباق پله‌های فرار، عایق کاری رطوبتی و ضخامت دیواری",
                status = "برنامه‌ریزی شده (نسخه ۲.۵)",
                description = "فرمولاسیون هوشمند طراحی طول بازوی رمپ پارکینگ مطابق مبحث ۴، انطباق ضوابط معلولین با نقشه‌ها، ثبت نتایج عایق‌کاری رطوبتی بام و مغایرت دیوار بر اساس مبحث ۱۹.",
                icon = Icons.Default.Star,
                accentColor = Color(0xFF10B981)
            )
        }
    }
}

@Composable
fun RoadmapItem(
    phase: String,
    title: String,
    status: String,
    description: String,
    icon: ImageVector,
    accentColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                    }
                    Text(phase, color = accentColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(status, color = Color.LightGray, fontSize = 9.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(description, color = Color.Gray, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
