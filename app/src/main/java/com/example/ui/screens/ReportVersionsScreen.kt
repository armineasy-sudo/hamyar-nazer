package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.data.ReportVersionEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportVersionsScreen(
    versions: List<ReportVersionEntity>,
    onNavigateBack: () -> Unit
) {
    var selectedVersion by remember { mutableStateOf<ReportVersionEntity?>(null) }
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("نسخه‌های گزارش", fontWeight = FontWeight.Bold, color = Color.White) },
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
        if (versions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("هنوز نسخه‌ای ذخیره نشده است.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(versions, key = { it.id }) { version ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        onClick = { selectedVersion = version }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "نسخه ${version.versionNumber}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                                        .format(Date(version.createdAt)),
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                            if (version.isFinal) {
                                Text("نسخه نهایی", color = Color(0xFF10B981), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        selectedVersion?.let { version ->
            AlertDialog(
                onDismissRequest = { selectedVersion = null },
                title = { Text("نسخه ${version.versionNumber}", color = Color.White, fontWeight = FontWeight.Bold) },
                containerColor = Color(0xFF1E293B),
                confirmButton = {
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(version.content))
                            selectedVersion = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Text("کپی")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedVersion = null }) {
                        Text("بستن", color = Color.LightGray)
                    }
                },
                text = {
                    OutlinedTextField(
                        value = version.content,
                        onValueChange = {},
                        readOnly = true,
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
            )
        }
    }
}
