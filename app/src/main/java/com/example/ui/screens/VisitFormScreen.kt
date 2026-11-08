package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.InspectionVisitEntity
import com.example.data.ProjectEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitFormScreen(
    project: ProjectEntity,
    visits: List<InspectionVisitEntity>,
    onSaveVisit: (InspectionVisitEntity) -> Unit,
    onNavigateBack: () -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var editingVisit by remember { mutableStateOf<InspectionVisitEntity?>(null) }

    var visitDate by remember { mutableStateOf("") }
    var visitTime by remember { mutableStateOf("") }
    var executionStage by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    fun openForm(visit: InspectionVisitEntity? = null) {
        editingVisit = visit
        visitDate = visit?.visitDateShamsi ?: ""
        visitTime = visit?.visitTime ?: ""
        executionStage = visit?.executionStage ?: ""
        notes = visit?.notes ?: ""
        showForm = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("پیشرفت پروژه", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        Text(project.name, color = Color.LightGray, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت", tint = Color.White)
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
                Text("هنوز بازدیدی ثبت نشده است.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(visits) { visit ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        onClick = { openForm(visit) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = visit.visitDateShamsi,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = visit.executionStage,
                                color = Color(0xFF3B82F6),
                                fontSize = 13.sp
                            )
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
                            if (visitDate.isNotBlank() && executionStage.isNotBlank()) {
                                val visit = InspectionVisitEntity(
                                    id = editingVisit?.id ?: 0,
                                    projectId = project.id,
                                    visitDateShamsi = visitDate,
                                    visitTime = visitTime.ifBlank { null },
                                    executionStage = executionStage,
                                    notes = notes.ifBlank { null },
                                    status = "COMPLETED"
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
                    TextButton(onClick = { showForm = false }) {
                        Text("انصراف", color = Color.LightGray)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = visitDate,
                            onValueChange = { visitDate = it },
                            label = { Text("تاریخ بازدید (شمسی)", color = Color.LightGray) },
                            placeholder = { Text("مثال: 1404/08/15", color = Color.Gray) },
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
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = executionStage,
                            onValueChange = { executionStage = it },
                            label = { Text("مرحله اجرا", color = Color.LightGray) },
                            placeholder = { Text("مثال: لوله‌کشی طبقه اول", color = Color.Gray) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

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
    }
}
