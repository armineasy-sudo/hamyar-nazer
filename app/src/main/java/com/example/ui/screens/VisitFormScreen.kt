package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChecklistEntity
import com.example.data.InspectionVisitEntity
import com.example.data.ProjectEntity
import com.example.data.ReportVersionEntity
import com.example.service.GeminiService
import com.example.ui.components.ShamsiDatePickerDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitFormScreen(
    project: ProjectEntity,
    visits: List<InspectionVisitEntity>,
    checklists: List<ChecklistEntity> = emptyList(),
    onSaveVisit: (InspectionVisitEntity) -> Unit,
    onDeleteVisit: (InspectionVisitEntity) -> Unit = {},
    onSaveReportVersion: (ReportVersionEntity) -> Unit = {},
    onNavigateToReportVersions: () -> Unit = {},
    onNavigateBack: () -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var editingVisit by remember { mutableStateOf<InspectionVisitEntity?>(null) }
    var showReportDialog by remember { mutableStateOf(false) }
    var reportText by remember { mutableStateOf("") }
    var includeRegulations by remember { mutableStateOf(false) }
    var isGenerating by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedVisitDateForReport by remember { mutableStateOf("") }
    var currentVisitIdForReport by remember { mutableStateOf(0L) }

    var visitDate by remember { mutableStateOf("") }
    var visitTime by remember { mutableStateOf("") }
    var executionStage by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("COMPLETED") }
    var dateError by remember { mutableStateOf(false) }
    var stageError by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    fun openForm(visit: InspectionVisitEntity? = null) {
        editingVisit = visit
        visitDate = visit?.visitDateShamsi ?: ""
        visitTime = visit?.visitTime ?: ""
        executionStage = visit?.executionStage ?: ""
        notes = visit?.notes ?: ""
        status = visit?.status ?: "COMPLETED"
        dateError = false
        stageError = false
        showForm = true
    }

    fun generateAiReport(date: String = "", visitId: Long = 0L) {
        isGenerating = true
        scope.launch {
            reportText = GeminiService.generateSupervisionReport(
                project = project,
                checklists = checklists,
                visitDateShamsi = date.ifBlank { selectedVisitDateForReport },
                includeRegulations = includeRegulations
            )
            isGenerating = false
            showReportDialog = true
            currentVisitIdForReport = visitId
        }
    }

    fun saveCurrentReportAsVersion() {
        if (reportText.isNotBlank() && currentVisitIdForReport > 0) {
            val version = ReportVersionEntity(
                visitId = currentVisitIdForReport,
                versionNumber = 1,
                content = reportText,
                tone = "SAFETY_WARNING"
            )
            onSaveReportVersion(version)
        }
    }

    fun validate(): Boolean {
        dateError = visitDate.isBlank()
        stageError = executionStage.isBlank()
        return !dateError && !stageError
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("پیشرفت پروژه", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        Text(
                            "${project.name} — گروه ${project.selectedBuildingGroup}",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToReportVersions) {
                        Icon(Icons.Default.History, contentDescription = "نسخه‌های گزارش", tint = Color.White)
                    }
                    IconButton(
                        onClick = {
                            val latestVisit = visits.firstOrNull()
                            selectedVisitDateForReport = latestVisit?.visitDateShamsi ?: ""
                            generateAiReport(selectedVisitDateForReport, latestVisit?.id ?: 0L)
                        },
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "تولید گزارش هوشمند", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openForm() },
                containerColor = Color(0xFF3B82F6),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "ثبت بازدید جدید")
            }
        },
        containerColor = Color(0xFF0F172A)
    ) { padding ->
        if (visits.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("هنوز بازدیدی ثبت نشده است.", color = Color.Gray)
                    Text("با دکمه + بازدید جدید ثبت کنید.", color = Color.Gray, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "تاریخچه بازدیدها (${visits.size})",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(visits, key = { it.id }) { visit ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        onClick = { openForm(visit) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = visit.visitDateShamsi,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (visit.status == "COMPLETED") "تکمیل" else "پیش‌نویس",
                                    color = if (visit.status == "COMPLETED") Color(0xFF10B981) else Color(0xFFF59E0B),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = visit.executionStage,
                                color = Color(0xFF3B82F6),
                                fontSize = 13.sp
                            )
                            if (!visit.visitTime.isNullOrBlank()) {
                                Text(
                                    text = "ساعت: ${visit.visitTime}",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                            if (!visit.notes.isNullOrBlank()) {
                                Text(
                                    text = visit.notes,
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showForm) {
            AlertDialog(
                onDismissRequest = { showForm = false },
                title = {
                    Text(
                        if (editingVisit == null) "ثبت پیشرفت جدید" else "ویرایش پیشرفت",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                containerColor = Color(0xFF1E293B),
                confirmButton = {
                    Button(
                        onClick = {
                            if (validate()) {
                                val visit = InspectionVisitEntity(
                                    id = editingVisit?.id ?: 0,
                                    projectId = project.id,
                                    visitDateShamsi = visitDate.trim(),
                                    visitTime = visitTime.trim().ifBlank { null },
                                    executionStage = executionStage.trim(),
                                    notes = notes.trim().ifBlank { null },
                                    status = status
                                )
                                onSaveVisit(visit)
                                showForm = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Text("ذخیره")
                    }
                },
                dismissButton = {
                    Row {
                        if (editingVisit != null) {
                            TextButton(
                                onClick = {
                                    onDeleteVisit(editingVisit!!)
                                    showForm = false
                                }
                            ) {
                                Text("حذف", color = Color(0xFFEF4444))
                            }
                        }
                        TextButton(onClick = { showForm = false }) {
                            Text("انصراف", color = Color.LightGray)
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = visitDate,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("تاریخ بازدید (شمسی)", color = Color.LightGray) },
                            placeholder = { Text("برای انتخاب تاریخ کلیک کنید", color = Color.Gray) },
                            isError = dateError,
                            supportingText = if (dateError) {
                                { Text("تاریخ الزامی است", color = Color(0xFFEF4444)) }
                            } else null,
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = "انتخاب تاریخ", tint = Color(0xFF3B82F6))
                                }
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = visitTime,
                            onValueChange = { visitTime = it },
                            label = { Text("ساعت (اختیاری)", color = Color.LightGray) },
                            placeholder = { Text("مثال: 10:30", color = Color.Gray) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = executionStage,
                            onValueChange = { executionStage = it; stageError = false },
                            label = { Text("مرحله اجرا", color = Color.LightGray) },
                            placeholder = { Text("مثال: لوله‌کشی طبقه اول", color = Color.Gray) },
                            isError = stageError,
                            supportingText = if (stageError) {
                                { Text("مرحله اجرا الزامی است", color = Color(0xFFEF4444)) }
                            } else null,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("وضعیت:", color = Color.LightGray, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = status == "DRAFT",
                                onClick = { status = "DRAFT" },
                                label = { Text("پیش‌نویس") }
                            )
                            FilterChip(
                                selected = status == "COMPLETED",
                                onClick = { status = "COMPLETED" },
                                label = { Text("تکمیل‌شده") }
                            )
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("یادداشت", color = Color.LightGray) },
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            )
        }

        if (showDatePicker) {
            ShamsiDatePickerDialog(
                initialDate = visitDate,
                onDateSelected = { selected ->
                    visitDate = selected
                    dateError = false
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }

        if (showReportDialog) {
            AlertDialog(
                onDismissRequest = { showReportDialog = false },
                title = { Text("پیش‌نویس گزارش هوشمند", color = Color.White, fontWeight = FontWeight.Bold) },
                containerColor = Color(0xFF1E293B),
                confirmButton = {
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(reportText))
                            saveCurrentReportAsVersion()
                            showReportDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Text("کپی و ذخیره نسخه")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReportDialog = false }) {
                        Text("بستن", color = Color.LightGray)
                    }
                },
                text = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Checkbox(
                                checked = includeRegulations,
                                onCheckedChange = {
                                    includeRegulations = it
                                    generateAiReport(selectedVisitDateForReport, currentVisitIdForReport)
                                }
                            )
                            Text("ارجاع به مقررات", color = Color.White, fontSize = 13.sp)
                        }
                        OutlinedTextField(
                            value = reportText,
                            onValueChange = { reportText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                                .verticalScroll(rememberScrollState()),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                        )
                    }
                }
            )
        }
    }
}
