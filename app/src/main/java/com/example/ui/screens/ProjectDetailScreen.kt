package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChecklistEntity
import com.example.data.ProjectEntity
import com.example.service.GeminiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    project: ProjectEntity,
    checklists: List<ChecklistEntity>,
    onUpdateChecklist: (ChecklistEntity) -> Unit,
    onArchiveProject: (ProjectEntity) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Checklists, 1 = AI Report, 2 = Drawing Review
    var selectedCategory by remember { mutableStateOf("BEFORE_CONSTRUCTION") } // BEFORE_CONSTRUCTION, DURING_CONSTRUCTION, BEFORE_FINAL_APPROVAL
    
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Checklist Edit Dialog state
    var editingItem by remember { mutableStateOf<ChecklistEntity?>(null) }
    var itemNote by remember { mutableStateOf("") }
    var itemStatus by remember { mutableStateOf("") }

    // AI Report states
    var generatedReportText by remember { mutableStateOf("") }
    var isGeneratingReport by remember { mutableStateOf(false) }

    // Drawings states
    var selectedFileType by remember { mutableStateOf("BOILER_ROOM") } // BOILER_ROOM, WATER_MUNICIPAL, CHIMNEY_DUCT, FIRE_PROTECTION
    var isAnalyzingDrawing by remember { mutableStateOf(false) }
    var drawingResultText by remember { mutableStateOf("") }
    var uploadFileName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(project.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        Text(
                            text = "${project.city}، منطقه ${project.district} | ${project.floorsCount} طبقه | گروه ساختاری ${project.buildingGroup}", 
                            color = Color.LightGray, 
                            fontSize = 10.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "بازگشت", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val updatedPrj = project.copy(status = if (project.status == "ACTIVE") "ARCHIVED" else "ACTIVE")
                            onArchiveProject(updatedPrj)
                            Toast.makeText(context, "وضعیت بایگانی پرونده بروزرسانی شد.", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text(
                            text = if (project.status == "ACTIVE") "بایگانی کامل" else "فعال‌سازی مجدد",
                            color = Color(0xFFEF4444),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
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
        ) {
            // Screen Navigation Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF1E293B),
                contentColor = Color(0xFF3B82F6),
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("چک‌لیست مکانیکی", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.Gray
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("گزارش‌ساز AI", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.Gray
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("بررسی نقشه با AI", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // TAB CONTENT REDIRECTIONS
            when (selectedTab) {
                0 -> {
                    // TAB 0: SMART CHECKLISTS
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)) {
                        // Category pill buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            CategoryPill(
                                title = "شروع ساخت",
                                isSelected = selectedCategory == "BEFORE_CONSTRUCTION",
                                onClick = { selectedCategory = "BEFORE_CONSTRUCTION" },
                                modifier = Modifier.weight(1f)
                            )
                            CategoryPill(
                                title = "حین عملیات",
                                isSelected = selectedCategory == "DURING_CONSTRUCTION",
                                onClick = { selectedCategory = "DURING_CONSTRUCTION" },
                                modifier = Modifier.weight(1f)
                            )
                            CategoryPill(
                                title = "پایان کار",
                                isSelected = selectedCategory == "BEFORE_FINAL_APPROVAL",
                                onClick = { selectedCategory = "BEFORE_FINAL_APPROVAL" },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val filteredChecklists = checklists.filter { it.category == selectedCategory }

                        if (filteredChecklists.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("آیتمی در این طبقه‌بندی یافت نشد.", color = Color.Gray, fontSize = 12.sp)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filteredChecklists) { item ->
                                    ChecklistRowItem(
                                        item = item,
                                        onClick = {
                                            editingItem = item
                                            itemNote = item.inspectorNotes
                                            itemStatus = item.status
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: AI REPORT GENERATOR
                    val reportScrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(reportScrollState),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "تولید خودکار گزارش فنی مهندسی",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "این افزونه بر اساس آخرین تغییر جزییات چک‌لیست‌های تایید شده و دارای نقص، یک متن اداری رسمی سازگار با سامانه نظام مهندسی با هوش مصنوعی آماده می‌کند.",
                                    color = Color.LightGray,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        isGeneratingReport = true
                                        scope.launch {
                                            generatedReportText = GeminiService.generateSupervisionReport(project, checklists)
                                            isGeneratingReport = false
                                        }
                                    },
                                    enabled = !isGeneratingReport,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (isGeneratingReport) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("درحال بررسی اسناد و نگارش هوشمند...", fontSize = 12.sp, color = Color.White)
                                    } else {
                                        Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("نگارش و تنظیم پیش‌نویس با AI", fontSize = 12.sp, color = Color.White)
                                    }
                                }
                            }
                        }

                        if (generatedReportText.isNotEmpty()) {
                            Text(
                                "پیش‌نویس گزارش نظارتی تاسیسات مکانیکی (کاملاً قابل ویرایش)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )

                            OutlinedTextField(
                                value = generatedReportText,
                                onValueChange = { generatedReportText = it },
                                textStyle = TextStyle(
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF10B981),
                                    unfocusedBorderColor = Color.DarkGray,
                                    focusedContainerColor = Color(0xFF1E293B),
                                    unfocusedContainerColor = Color(0xFF1E293B),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                minLines = 10,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(generatedReportText))
                                        Toast.makeText(context, "متن گزارش کپی شد و آماده پیست در سایت نظارت است.", Toast.LENGTH_LONG).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1.5f)
                                ) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("کپی متن گزارش کارگاه", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        generatedReportText = ""
                                        Toast.makeText(context, "گزارش پاک شد.", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray),
                                    border = BorderStroke(1.dp, Color.DarkGray),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("حذف پیش‌نویس", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: BLUEPRINT ANNOTATOR / MECHANICAL DRAWINGS ASSISTANT
                    val drScroll = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(drScroll),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "سیستم هوشمند انطباق‌سنج نقشه‌ها",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Select Plan types
                                Text("انتخاب پلان مورد بررسی:", color = Color.Gray, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    PlanTypePill(
                                        title = "موتورخانه",
                                        isSelected = selectedFileType == "BOILER_ROOM",
                                        onClick = { selectedFileType = "BOILER_ROOM" },
                                        modifier = Modifier.weight(1f)
                                    )
                                    PlanTypePill(
                                        title = "آبرسانی",
                                        isSelected = selectedFileType == "WATER_MUNICIPAL",
                                        onClick = { selectedFileType = "WATER_MUNICIPAL" },
                                        modifier = Modifier.weight(1f)
                                    )
                                    PlanTypePill(
                                        title = "دودکش",
                                        isSelected = selectedFileType == "CHIMNEY_DUCT",
                                        onClick = { selectedFileType = "CHIMNEY_DUCT" },
                                        modifier = Modifier.weight(1f)
                                    )
                                    PlanTypePill(
                                        title = "اطفاء حریق",
                                        isSelected = selectedFileType == "FIRE_PROTECTION",
                                        onClick = { selectedFileType = "FIRE_PROTECTION" },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Dashed Drawing Simulator Upload Box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .border(
                                            BorderStroke(2.dp, Color(0xFF3B82F6)),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                                        .clickable {
                                            uploadFileName = when (selectedFileType) {
                                                "BOILER_ROOM" -> "BoilerRoom-Plan-Rev3_Floor-B2.pdf"
                                                "WATER_MUNICIPAL" -> "SanitaryWater-WaterMetreRiser-V2.pdf"
                                                "CHIMNEY_DUCT" -> "Chimney-KitchenRiserGas_Section.pdf"
                                                "FIRE_PROTECTION" -> "FireSprinkler-HydrantZoning-01.pdf"
                                                else -> "Blueprint.pdf"
                                            }
                                            Toast
                                                .makeText(
                                                    context,
                                                    "نقشه مصوب با موفقیت بارگذاری شد.",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (uploadFileName.isEmpty()) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.AddCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF3B82F6),
                                                modifier = Modifier.size(36.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                "بارگذاری نقشه‌های تاسیسات (فرمت PDF / CAD)",
                                                color = Color.LightGray,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                            Text(
                                                "برای شبیه‌سازی واردکردن نقشه، ضربه بزنید.",
                                                color = Color.Gray,
                                                fontSize = 9.sp,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    } else {
                                        // Visual simulation of loaded details
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Done,
                                                contentDescription = null,
                                                tint = Color(0xFF10B981),
                                                modifier = Modifier.size(32.dp)
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(uploadFileName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            Text("نقشه مکانیکی تایید شده آماده واکاوی مغایرت‌ها", color = Color.Gray, fontSize = 9.sp, modifier = Modifier.padding(top = 2.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        if (uploadFileName.isEmpty()) {
                                            Toast.makeText(context, "لطفا ابتدا با کلیک روی کادر، نقشه را لود فرضی کنید.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            isAnalyzingDrawing = true
                                            scope.launch {
                                                val details = when (selectedFileType) {
                                                    "BOILER_ROOM" -> "۲ عدد دیگ چدنی شوفاژ پکیج، پمپ گردش ۴ اینچ، مخزن انبساط بسته فاقد لوله سرریز مستقیم به اتمسفر"
                                                    "WATER_MUNICIPAL" -> "کلکتور برگشت بهداشتی گالوانیزه ۱.۵ اینچ، لوله توزیع آب سرد ۲ اینچ ساختمان"
                                                    "CHIMNEY_DUCT" -> "مسیر دودکشی آشپزخانه متقاطع با کانال تخلیه هواکش توالت"
                                                    "FIRE_PROTECTION" -> "کلکتور اسپرینکلر مجهز به شیرهای ایزوله و پرشر سوویچ بدون هدر تخلیه ثانویه"
                                                    else -> "تجهیزات گوناگون"
                                                }
                                                drawingResultText = GeminiService.analyzeMechanicalDrawing(selectedFileType, details)
                                                isAnalyzingDrawing = false
                                            }
                                        }
                                    },
                                    enabled = !isAnalyzingDrawing,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (isAnalyzingDrawing) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("در حال بررسی متن و تحلیل انطباق با مباحث مقررات...", fontSize = 12.sp, color = Color.White)
                                    } else {
                                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("واکاوی و اشکال‌زدایی اتوماتیک نقشه با AI", fontSize = 12.sp, color = Color.White)
                                    }
                                }
                            }
                        }

                        if (drawingResultText.isNotEmpty()) {
                            Text(
                                "گزارش همگام‌سازی و مغایرت‌های فنی مقررات ملی ساختمان",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )

                            // Highlighted Warnings box based on generated simulation
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = drawingResultText,
                                        color = Color.LightGray,
                                        fontSize = 11.sp,
                                        lineHeight = 17.sp,
                                        textAlign = TextAlign.Right
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    drawingResultText = ""
                                    uploadFileName = ""
                                    Toast.makeText(context, "آنالیز نقشه ریست شد.", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("تخلیه نتایج واکاوی", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Checklist Detail Editing Dialog
        if (editingItem != null) {
            val item = editingItem!!
            AlertDialog(
                onDismissRequest = { editingItem = null },
                title = { Text("ویرایش وضعیت و ثبت نظر ناظر", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White) },
                containerColor = Color(0xFF1E293B),
                confirmButton = {
                    Button(
                        onClick = {
                            val updated = item.copy(
                                status = itemStatus,
                                inspectorNotes = itemNote
                            )
                            onUpdateChecklist(updated)
                            editingItem = null
                            Toast.makeText(context, "تغییرات با موفقیت ذخیره شد.", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Text("ثبت تغییرات", color = Color.White, fontSize = 11.sp)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingItem = null }) {
                        Text("انصراف", color = Color.LightGray, fontSize = 11.sp)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(item.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 16.sp)
                        Text(
                            text = "مرجع مقررات ملی: ${item.regulationReference} | اولویت ایمنی: ${item.priority}",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )

                        HorizontalDivider(color = Color.DarkGray)

                        // Status radio simulator buttons
                        Text("تغییر وضعیت آزمون:", color = Color.LightGray, fontSize = 11.sp)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (itemStatus == "COMPLETED") Color(0xFF10B981).copy(alpha = 0.15f) else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { itemStatus = "COMPLETED" }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = itemStatus == "COMPLETED", onClick = { itemStatus = "COMPLETED" })
                                Text("تکمیل شده و مورد تایید ناظر تاسیسات", color = Color.White, fontSize = 11.sp)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (itemStatus == "REQUIRES_CORRECTION") Color(0xFFEF4444).copy(alpha = 0.15f) else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { itemStatus = "REQUIRES_CORRECTION" }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = itemStatus == "REQUIRES_CORRECTION", onClick = { itemStatus = "REQUIRES_CORRECTION" })
                                Text("مشاهده نقص کارگاهی و لزوم اصلاح مجدد", color = Color.White, fontSize = 11.sp)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (itemStatus == "NOT_COMPLETED") Color.Gray.copy(alpha = 0.15f) else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { itemStatus = "NOT_COMPLETED" }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = itemStatus == "NOT_COMPLETED", onClick = { itemStatus = "NOT_COMPLETED" })
                                Text("عدم اجرا / در حال بررسی در مراحل آتی", color = Color.White, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Text Comment
                        OutlinedTextField(
                            value = itemNote,
                            onValueChange = { itemNote = it },
                            label = { Text("ثبت جزئیات عدم انطباق یا یادداشت فنی", color = Color.LightGray, fontSize = 10.sp) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = if (isSelected) Color(0xFF3B82F6) else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Composable
fun PlanTypePill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = if (isSelected) Color(0xFF3B82F6).copy(alpha = 0.15f) else Color(0xFF0F172A),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                BorderStroke(1.dp, if (isSelected) Color(0xFF3B82F6) else Color.Transparent),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color(0xFF3B82F6) else Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
        )
    }
}

@Composable
fun ChecklistRowItem(
    item: ChecklistEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مرجع: ${item.regulationReference}",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )

                        Text(
                            text = "اولویت: ${item.priority}",
                            color = when (item.priority) {
                                "CRITICAL" -> Color(0xFFEF4444)
                                "HIGH" -> Color(0xFFF59E0B)
                                else -> Color(0xFF3B82F6)
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .background(
                            color = when (item.status) {
                                "COMPLETED" -> Color(0xFF10B981).copy(alpha = 0.15f)
                                "REQUIRES_CORRECTION" -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                else -> Color.Gray.copy(alpha = 0.15f)
                            },
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (item.status) {
                            "COMPLETED" -> "تایید شد"
                            "REQUIRES_CORRECTION" -> "نیاز به اصلاح"
                            else -> "عدم اجرا"
                        },
                        color = when (item.status) {
                            "COMPLETED" -> Color(0xFF10B981)
                            "REQUIRES_CORRECTION" -> Color(0xFFEF4444)
                            else -> Color.Gray
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (item.inspectorNotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.inspectorNotes,
                            color = Color.LightGray,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
