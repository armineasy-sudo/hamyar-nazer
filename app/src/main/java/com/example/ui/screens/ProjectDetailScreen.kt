package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProjectEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    project: ProjectEntity,
    onNavigateToVisits: () -> Unit,
    onArchiveProject: (ProjectEntity) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(project.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val updated = project.copy(
                                status = if (project.status == "ACTIVE") "ARCHIVED" else "ACTIVE"
                            )
                            onArchiveProject(updated)
                        }
                    ) {
                        Text(
                            text = if (project.status == "ACTIVE") "بایگانی" else "فعال‌سازی",
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Project Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("اطلاعات پروژه", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    InfoRow("نام پروژه", project.name)
                    InfoRow("مالک", project.ownerName ?: "—")
                    InfoRow("شهر", project.city)
                    InfoRow("منطقه", project.district ?: "—")
                    InfoRow("نوع کاربری", project.buildingType)
                    InfoRow("تعداد طبقات", "${project.floorsCount}")
                    InfoRow("متراژ مؤثر", "${project.effectiveArea.toInt()} مترمربع")
                    InfoRow("گروه پیشنهادی", project.suggestedBuildingGroup)
                    InfoRow("گروه نهایی", project.selectedBuildingGroup)
                    if (project.groupChangedByUser) {
                        Text(
                            "گروه توسط ناظر تغییر یافته است",
                            color = Color(0xFFF59E0B),
                            fontSize = 12.sp
                        )
                    }
                    InfoRow("وضعیت", if (project.status == "ACTIVE") "فعال" else "بایگانی")
                }
            }

            // Actions
            Button(
                onClick = onNavigateToVisits,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ثبت و مشاهده پیشرفت پروژه", fontSize = 16.sp)
            }

            if (!project.notes.isNullOrBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("یادداشت", color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(project.notes, color = Color.LightGray, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
