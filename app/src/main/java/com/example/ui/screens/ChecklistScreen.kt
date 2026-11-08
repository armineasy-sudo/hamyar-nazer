package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChecklistEntity
import com.example.data.ProjectEntity
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    project: ProjectEntity,
    checklists: List<ChecklistEntity>,
    onUpdateChecklist: (ChecklistEntity) -> Unit,
    onNavigateBack: () -> Unit
) {
    val categories = listOf(
        "SAFETY" to "ایمنی و مجری ذی‌صلاح",
        "BEFORE_CONSTRUCTION" to "قبل از اجرا",
        "DURING_CONSTRUCTION" to "حین اجرا",
        "BEFORE_FINAL_APPROVAL" to "قبل از تأیید نهایی"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("چک‌لیست نظارت", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
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
        containerColor = Color(0xFF0F172A)
    ) { padding ->
        if (checklists.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("چک‌لیستی یافت نشد.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                categories.forEach { (cat, title) ->
                    val items = checklists.filter { it.category == cat }
                    if (items.isNotEmpty()) {
                        item {
                            Text(title, color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                        }
                        items(items, key = { it.id }) { item ->
                            ChecklistItemCard(
                                item = item,
                                onStatusChange = { newStatus ->
                                    onUpdateChecklist(item.copy(status = newStatus))
                                },
                                onPhotoSelected = { path ->
                                    onUpdateChecklist(item.copy(photoPath = path))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChecklistItemCard(
    item: ChecklistEntity,
    onStatusChange: (String) -> Unit,
    onPhotoSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val statuses = listOf("NOT_COMPLETED" to "بررسی‌نشده", "COMPLETED" to "تأیید", "REQUIRES_CORRECTION" to "نیاز به اصلاح")
    val statusColor = when (item.status) {
        "COMPLETED" -> Color(0xFF10B981)
        "REQUIRES_CORRECTION" -> Color(0xFFEF4444)
        else -> Color.Gray
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val file = File(context.filesDir, "checklist_${item.id}_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, out)
            }
            onPhotoSelected(file.absolutePath)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(item.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            if (item.regulationReference.isNotBlank()) {
                Text(item.regulationReference, color = Color(0xFF94A3B8), fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                statuses.forEach { (value, label) ->
                    FilterChip(
                        selected = item.status == value,
                        onClick = { onStatusChange(value) },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = statusColor.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { cameraLauncher.launch(null) },
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("پیوست عکس", fontSize = 11.sp)
                }

                if (!item.photoPath.isNullOrBlank()) {
                    Text("عکس ذخیره شد", color = Color(0xFF10B981), fontSize = 11.sp)
                }
            }
        }
    }
}
