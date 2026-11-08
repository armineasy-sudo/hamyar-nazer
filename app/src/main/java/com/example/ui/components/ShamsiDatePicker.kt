package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShamsiDatePickerDialog(
    initialDate: String = "",
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val parts = initialDate.split("/").mapNotNull { it.toIntOrNull() }
    var year by remember { mutableStateOf(parts.getOrNull(0) ?: 1404) }
    var month by remember { mutableStateOf(parts.getOrNull(1) ?: 1) }
    var day by remember { mutableStateOf(parts.getOrNull(2) ?: 1) }

    val months = listOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    val daysInMonth = when {
        month <= 6 -> 31
        month <= 11 -> 30
        else -> 29 // سال کبیسه ساده‌سازی شده
    }

    if (day > daysInMonth) day = daysInMonth

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("انتخاب تاریخ شمسی", fontWeight = FontWeight.Bold, color = Color.White) },
        containerColor = Color(0xFF1E293B),
        confirmButton = {
            Button(
                onClick = {
                    val formatted = "%d/%02d/%02d".format(year, month, day)
                    onDateSelected(formatted)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
            ) {
                Text("تأیید")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف", color = Color.LightGray)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Year
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("سال:", color = Color.LightGray)
                    IconButton(onClick = { if (year > 1300) year-- }) {
                        Text("−", color = Color.White, fontSize = 20.sp)
                    }
                    Text("$year", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = { if (year < 1500) year++ }) {
                        Text("+", color = Color.White, fontSize = 20.sp)
                    }
                }

                // Month
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ماه:", color = Color.LightGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = { if (month > 1) month-- else month = 12 }) {
                            Text("◀", color = Color.White)
                        }
                        Text(
                            months[month - 1],
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        IconButton(onClick = { if (month < 12) month++ else month = 1 }) {
                            Text("▶", color = Color.White)
                        }
                    }
                }

                // Day
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("روز:", color = Color.LightGray)
                    IconButton(onClick = { if (day > 1) day-- }) {
                        Text("−", color = Color.White, fontSize = 20.sp)
                    }
                    Text("$day", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = { if (day < daysInMonth) day++ }) {
                        Text("+", color = Color.White, fontSize = 20.sp)
                    }
                }

                Text(
                    text = "تاریخ انتخابی: %d/%02d/%02d".format(year, month, day),
                    color = Color(0xFF3B82F6),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    )
}
