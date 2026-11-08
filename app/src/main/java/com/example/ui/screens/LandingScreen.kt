package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LandingScreen(
    onEnterApp: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Logo placeholder area — لوگوی شرکت آتین انرژی
        Card(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("آتین", color = Color(0xFF1E3A8A), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("انرژی", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "همیار ناظر",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Hamyar Nazer",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "دستیار هوشمند مهندس ناظر ساختمان",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "سازنده",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("مهندس آرمین ایزی")
                Text("شرکت همتا انرژی سازان آتین")
                Text("AtinENERGY.ir", color = Color(0xFFEF4444), fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "خدمات مجموعه آتین انرژی",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("• اجرای تأسیسات مکانیکی ساختمان")
                Text("• اجرای لوله‌کشی گاز")
                Text("• اجرای سیستم‌های اعلام و اطفای حریق")
                Text("• اجرای سیستم‌های تهویه مطبوع")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(onClick = { uriHandler.openUri("https://armineasy.ir") }) {
                Text("armineasy.ir")
            }
            TextButton(onClick = { uriHandler.openUri("https://atinenergy.ir") }) {
                Text("atinenergy.ir")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onEnterApp,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("ورود به نرم‌افزار", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "مالکیت مادی و معنوی متعلق به شرکت همتا انرژی سازان آتین است.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
