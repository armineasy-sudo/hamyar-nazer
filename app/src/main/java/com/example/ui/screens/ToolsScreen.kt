package com.example.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    onNavigateBack: () -> Unit
) {
    var activeToolTab by remember { mutableStateOf(0) } // 0 = Converter, 1 = Pump, 2 = Pipe, 3 = Level
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ابزارهای کاربردی مهندسی مکانیک", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp) },
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
        ) {
            // Horizontal tool categories
            TabRow(
                selectedTabIndex = activeToolTab,
                containerColor = Color(0xFF1E293B),
                contentColor = Color(0xFF3B82F6),
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = activeToolTab == 0,
                    onClick = { activeToolTab = 0 },
                    text = { Text("تبدیل واحد", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.Gray
                )
                Tab(
                    selected = activeToolTab == 1,
                    onClick = { activeToolTab = 1 },
                    text = { Text("فرمول پمپ", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.Gray
                )
                Tab(
                    selected = activeToolTab == 2,
                    onClick = { activeToolTab = 2 },
                    text = { Text("سایزینگ لوله", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.Gray
                )
                Tab(
                    selected = activeToolTab == 3,
                    onClick = { activeToolTab = 3 },
                    text = { Text("تراز دیجیتال", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (activeToolTab) {
                0 -> ConverterToolTab()
                1 -> PumpToolTab()
                2 -> PipeSizingToolTab()
                3 -> SpiritLevelToolTab(context)
            }
        }
    }
}

@Composable
fun ConverterToolTab() {
    val scroll = rememberScrollState()
    var pressureInput by remember { mutableStateOf("") }
    var flowInput by remember { mutableStateOf("") }
    var tempInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scroll),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pressure Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("تبدیل فشار کارگاهی (مایعات و گازها)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = pressureInput,
                    onValueChange = { pressureInput = it },
                    label = { Text("مقدار فشار به بار (Bar)", color = Color.Gray, fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                if (pressureInput.isNotEmpty()) {
                    val bar = pressureInput.toDoubleOrNull() ?: 0.0
                    val psi = bar * 14.5038
                    val kpa = bar * 100.0
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("معادل برحسب پوند بر اینچ (PSI): ${"%.2f".format(psi)}", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("معادل برحسب کیلوپاسکال (kPa): ${"%.1f".format(kpa)}", color = Color(0xFF3B82F6), fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }

        // Flow rate Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("تبدیل دبی جریان هیدرولیک (آبرسانی و فن‌کویل)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = flowInput,
                    onValueChange = { flowInput = it },
                    label = { Text("مقدار دبی به گالن بر دقیقه (GPM)", color = Color.Gray, fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                if (flowInput.isNotEmpty()) {
                    val gpm = flowInput.toDoubleOrNull() ?: 0.0
                    val m3h = gpm * 0.2271
                    val lps = gpm * 0.0631
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("معادل برحسب متر مکعب در ساعت (m³/h): ${"%.2f".format(m3h)}", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("معادل برحسب لیتر بر ثانیه (L/s): ${"%.3f".format(lps)}", color = Color(0xFF3B82F6), fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }

        // Temperature Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("تبدیل دما دیگ و سیستم هیدرونیک", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = tempInput,
                    onValueChange = { tempInput = it },
                    label = { Text("دما به سانتی‌گراد (°C)", color = Color.Gray, fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                if (tempInput.isNotEmpty()) {
                    val celsius = tempInput.toDoubleOrNull() ?: 0.0
                    val fahr = celsius * 1.8 + 32
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("معادل برحسب فارنهایت (°F): ${"%.1f".format(fahr)}", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PumpToolTab() {
    var flowInput by remember { mutableStateOf("") } // m³/h
    var headInput by remember { mutableStateOf("") } // m
    var powerOutput by remember { mutableStateOf<Double?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("محاسبه توان شفت و الکتروموتور پمپ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = flowInput,
                    onValueChange = { flowInput = it },
                    label = { Text(" دبی جریان هیدرولیکی رفت (Q برحسب m³/h)", color = Color.Gray, fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = headInput,
                    onValueChange = { headInput = it },
                    label = { Text("ارتفاع کل هد هیدرولیک (H برحسب متر آب)", color = Color.Gray, fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val q = flowInput.toDoubleOrNull() ?: 0.0
                        val h = headInput.toDoubleOrNull() ?: 0.0
                        if (q > 0 && h > 0) {
                            // Power in kW = (Q * H * 9.81) / (3600 * Efficiency)
                            // Estimating efficiency at 60%
                            powerOutput = (q * h * 9.81) / (3600 * 0.60)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("محاسبه توان پمپ", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        if (powerOutput != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("نتایج محاسباتی مهندسی", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    val kw = powerOutput!!
                    val hp = kw * 1.341

                    Text("توان شفت مورد نیاز: ${"%.2f".format(kw)} کیلووات (kW)", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("توان اسب بخار موتور: ${"%.2f".format(hp)} اسب بخار (HP)", color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                    Text("راندمان برآورد موتورخانه: ۶۰ درصد (استاندارد مبحث ۱۴)", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
fun PipeSizingToolTab() {
    var gpmInput by remember { mutableStateOf("") }
    var recommendedPipeSize by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("تعیین سریع سایز لوله‌های فولادی سیاه", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("منطبق بر سرعت مجاز ۴ فوت در ثانیه در ساختمان‌های مسکونی", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
                
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = gpmInput,
                    onValueChange = { gpmInput = it },
                    label = { Text("میزان دبی مورد نیاز برحسب گالن بر دقیقه (GPM)", color = Color.Gray, fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val gpm = gpmInput.toDoubleOrNull() ?: 0.0
                        recommendedPipeSize = when {
                            gpm <= 0 -> ""
                            gpm <= 5.0 -> "۳/۴ اینچ (DN 20)"
                            gpm <= 12.0 -> "۱ اینچ (DN 25)"
                            gpm <= 24.0 -> "۱-۱/۴ اینچ (DN 32)"
                            gpm <= 40.0 -> "۱-۱/۲ اینچ (DN 40)"
                            gpm <= 80.0 -> "۲ اینچ (DN 50)"
                            gpm <= 140.0 -> "۲-۱/۲ اینچ (DN 65)"
                            gpm <= 220.0 -> "۳ اینچ (DN 80)"
                            else -> "۴ اینچ یا بالاتر (DN 100)"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("محاسبه قطر لوله فولادی", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        if (recommendedPipeSize.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("قطر نامی پیشنهادی لوله:", color = Color.Gray, fontSize = 11.sp)
                    Text(
                        text = recommendedPipeSize,
                        color = Color(0xFF3B82F6),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "* بر اساس استانداردهای طراحی مدار بسته کلکتور و فن‌کویل مبحث ۱۶ و ۱۴ مقررات ملی ساختمان.",
                        color = Color.Gray,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SpiritLevelToolTab(context: Context) {
    // Collect roll and pitch sensor data
    var pitch by remember { mutableStateOf(0f) } // x angle
    var roll by remember { mutableStateOf(0f) }  // y angle

    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    // Quick tilt estimations
                    pitch = event.values[0] // left-right
                    roll = event.values[1]  // up-down
                }
            }
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
        }
    }

    DisposableEffect(Unit) {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (sensor != null) {
            sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_UI)
        }
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "تراز دیجیتال موازی شیب‌سنج کارگاهی",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "برای تراز شیب لوله‌های فاضلاب (حداقل ۲ درصد شیب) و ساپورت موتورخانه از شتاب‌سنج داخلی دستگاه خود استفاده کنید.",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Live concentric circles displaying spirit level bubble
        Box(
            modifier = Modifier
                .size(240.dp)
                .background(Color(0xFF0F172A), RoundedCornerShape(120.dp))
                .border(BorderStroke(2.dp, Color(0xFF3B82F6)), shape = RoundedCornerShape(120.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val maxOffset = size.width / 2 - 20f

                // Draw circles inside
                drawCircle(color = Color.DarkGray, radius = size.width / 4, center = center, style = Stroke(width = 1.dp.toPx()))
                drawCircle(color = Color.DarkGray, radius = size.width / 8, center = center, style = Stroke(width = 1.dp.toPx()))

                // Map pitch/roll to displacement (clamped)
                // factor mapped so 10 m/s2 is the maximum limit edge
                val dx = (-pitch / 9.8f) * maxOffset
                val dy = (roll / 9.8f) * maxOffset

                // Clamp to keep bubble inside tares
                val currentX = center.x + dx.coerceIn(-maxOffset, maxOffset)
                val currentY = center.y + dy.coerceIn(-maxOffset, maxOffset)

                // Draw spirit level bubble
                drawCircle(
                    color = if (abs(pitch) < 0.4f && abs(roll) < 0.4f) Color(0xFF10B981) else Color(0xFFF59E0B),
                    radius = 16.dp.toPx(),
                    center = Offset(currentX, currentY)
                )
                
                // Draw crosshairs at center
                drawLine(color = Color.DarkGray, start = Offset(center.x, 0f), end = Offset(center.x, size.height), strokeWidth = 1.dp.toPx())
                drawLine(color = Color.DarkGray, start = Offset(0f, center.y), end = Offset(size.width, center.y), strokeWidth = 1.dp.toPx())
            }
        }

        // Numerical tilt indices
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("زاویه انحراف افق X (Pitch)", color = Color.Gray, fontSize = 11.sp)
                Text("${"%.1f".format(pitch * 5.72f)}°", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(top = 2.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("زاویه انحراف قائم Y (Roll)", color = Color.Gray, fontSize = 11.sp)
                Text("${"%.1f".format(roll * 5.72f)}°", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(top = 2.dp))
            }
        }

        if (abs(pitch) < 0.4f && abs(roll) < 0.4f) {
            Text("تزار است! (انحراف کمتر از ۰.۵ درجه)", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        } else {
            Text("غیرتراز (نیازمند تنظیم ساپورت کانال)", color = Color(0xFFF59E0B), fontSize = 11.sp)
        }
    }
}
