package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProjectEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(
    projects: List<ProjectEntity>,
    onAddProject: (ProjectEntity) -> Unit,
    onProjectSelected: (ProjectEntity) -> Unit,
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    // Form states
    var projName by remember { mutableStateOf("") }
    var projOwner by remember { mutableStateOf("") }
    var projCity by remember { mutableStateOf("تهران") }
    var projDistrict by remember { mutableStateOf("") }
    var projType by remember { mutableStateOf("مسکونی") }
    var projFloors by remember { mutableStateOf("") }
    var projArea by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf("ج") }
    var groupChangedByUser by remember { mutableStateOf(false) }

    val floorsInt = projFloors.toIntOrNull() ?: 0
    val areaDouble = projArea.toDoubleOrNull() ?: 0.0

    // محاسبه گروه پیشنهادی طبق مبحث ۲
    val suggestedGroup = remember(floorsInt, areaDouble) {
        when {
            floorsInt <= 0 && areaDouble <= 0 -> ""
            floorsInt <= 2 && areaDouble <= 600 -> "الف"
            floorsInt in 3..5 || areaDouble in 601.0..2000.0 -> "ب"
            floorsInt in 6..10 || areaDouble in 2001.0..5000.0 -> "ج"
            floorsInt > 10 || areaDouble > 5000 -> "د"
            else -> "ج"
        }
    }

    LaunchedEffect(suggestedGroup) {
        if (!groupChangedByUser && suggestedGroup.isNotEmpty()) {
            selectedGroup = suggestedGroup
        }
    }

    val filteredProjects = projects.filter {
        val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) ||
                            it.city.contains(searchQuery, ignoreCase = true)
        val matchesTab = if (selectedTab == 0) it.status == "ACTIVE" else it.status == "ARCHIVED"
        matchesSearch && matchesTab
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("پروژه‌های تحت نظارت شما", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "بازگشت", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF3B82F6),
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "افزودن پروژه جدید")
            }
        },
        containerColor = Color(0xFF0F172A)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("جستجو در پروژه‌ها...", color = Color.Gray, fontSize = 13.sp) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3B82F6),
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF1E293B),
                contentColor = Color(0xFF3B82F6),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .padding(2.dp),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color.White
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("پرونده‌های فعال", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.Gray
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("پروژه‌های بایگانی", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredProjects.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedTab == 0) "هیچ پروژه فعالی یافت نشد." else "هیچ پروژه‌ای آرشیو نشده است.",
                        color = Color.LightGray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredProjects) { project ->
                        ProjectItem(project = project, onClick = { onProjectSelected(project) })
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("ثبت پروژه جدید", fontWeight = FontWeight.Bold, color = Color.White) },
                containerColor = Color(0xFF1E293B),
                confirmButton = {
                    Button(
                        onClick = {
                            if (projName.isNotBlank() && floorsInt > 0 && areaDouble > 0) {
                                val newProject = ProjectEntity(
                                    name = projName,
                                    ownerName = projOwner.ifBlank { null },
                                    city = projCity,
                                    district = projDistrict.ifBlank { null },
                                    buildingType = projType,
                                    floorsCount = floorsInt,
                                    effectiveArea = areaDouble,
                                    suggestedBuildingGroup = suggestedGroup,
                                    selectedBuildingGroup = selectedGroup,
                                    groupChangedByUser = groupChangedByUser,
                                    status = "ACTIVE"
                                )
                                onAddProject(newProject)
                                projName = ""
                                projOwner = ""
                                projDistrict = ""
                                projFloors = ""
                                projArea = ""
                                groupChangedByUser = false
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Text("ثبت پروژه", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("انصراف", color = Color.LightGray)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 480.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = projName,
                            onValueChange = { projName = it },
                            label = { Text("نام پروژه", color = Color.LightGray, fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = projOwner,
                            onValueChange = { projOwner = it },
                            label = { Text("نام مالک (اختیاری)", color = Color.LightGray, fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = projCity,
                                onValueChange = { projCity = it },
                                label = { Text("شهر", color = Color.LightGray, fontSize = 12.sp) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = projDistrict,
                                onValueChange = { projDistrict = it },
                                label = { Text("منطقه", color = Color.LightGray, fontSize = 12.sp) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = projType,
                            onValueChange = { projType = it },
                            label = { Text("نوع کاربری", color = Color.LightGray, fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = projFloors,
                                onValueChange = { projFloors = it },
                                label = { Text("تعداد طبقات (از شالوده)", color = Color.LightGray, fontSize = 12.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = projArea,
                                onValueChange = { projArea = it },
                                label = { Text("متراژ مؤثر (m²)", color = Color.LightGray, fontSize = 12.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (suggestedGroup.isNotEmpty()) {
                            Text(
                                text = "گروه پیشنهادی سیستم: $suggestedGroup",
                                color = Color(0xFF10B981),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text("گروه نهایی (قابل ویرایش):", color = Color.LightGray, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("الف", "ب", "ج", "د").forEach { group ->
                                FilterChip(
                                    selected = selectedGroup == group,
                                    onClick = {
                                        selectedGroup = group
                                        groupChangedByUser = true
                                    },
                                    label = { Text(group) }
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ProjectItem(
    project: ProjectEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = project.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = if (project.status == "ACTIVE") Color(0xFF10B981).copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (project.status == "ACTIVE") "فعال" else "بایگانی",
                        color = if (project.status == "ACTIVE") Color(0xFF10B981) else Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${project.city}${project.district?.let { " - منطقه $it" } ?: ""}",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Text(
                text = "${project.floorsCount} طبقه | گروه ${project.selectedBuildingGroup} | ${project.effectiveArea.toInt()} m²",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}
