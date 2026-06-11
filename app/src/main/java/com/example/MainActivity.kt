package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.AppDatabase
import com.example.data.ChecklistEntity
import com.example.data.ChecklistSeeder
import com.example.data.ProjectEntity
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

enum class Screen {
    LOGIN,
    DASHBOARD,
    PROJECTS_LIST,
    PROJECT_DETAIL,
    TOOLS,
    SETTINGS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Enforce Persian RTL Layout Direction
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }
    var engineerName by remember { mutableStateOf("") }
    var selectedProject by remember { mutableStateOf<ProjectEntity?>(null) }

    // Room Database connections
    val database = remember { AppDatabase.getDatabase(context) }
    val appDao = remember { database.appDao() }
    val scope = rememberCoroutineScope()

    // Query all projects
    val projectsList by remember { appDao.getAllProjects() }.collectAsState(initial = emptyList())

    // Fetch checklists for the selected project
    val activeProjectChecklists = remember(selectedProject) {
        if (selectedProject != null) {
            appDao.getChecklistsForProject(selectedProject!!.id)
        } else {
            kotlinx.coroutines.flow.flowOf(emptyList())
        }
    }.collectAsState(initial = emptyList())

    val activeCount = projectsList.count { it.status == "ACTIVE" }
    val archivedCount = projectsList.count { it.status == "ARCHIVED" }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            Screen.LOGIN -> {
                LoginScreen(
                    onLoginSuccess = { enteredName ->
                        engineerName = enteredName
                        currentScreen = Screen.DASHBOARD
                    }
                )
            }

            Screen.DASHBOARD -> {
                DashboardScreen(
                    engineerName = engineerName,
                    onNavigateToProjects = { currentScreen = Screen.PROJECTS_LIST },
                    onNavigateToTools = { currentScreen = Screen.TOOLS },
                    onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                    projectCount = activeCount,
                    archivedCount = archivedCount
                )
            }

            Screen.PROJECTS_LIST -> {
                ProjectListScreen(
                    projects = projectsList,
                    onAddProject = { name, city, district, group, type, floors, area, system ->
                        scope.launch {
                            val newProject = ProjectEntity(
                                name = name,
                                city = city,
                                district = district,
                                buildingGroup = group,
                                buildingType = type,
                                floorsCount = floors,
                                totalArea = area,
                                mechanicalSystemType = system,
                                status = "ACTIVE"
                            )
                            // Save project
                            val projectId = appDao.insertProject(newProject)
                            // Create and seed checklists
                            val seedChecklists = ChecklistSeeder.getDefaultChecklists(projectId)
                            appDao.insertChecklists(seedChecklists)
                        }
                    },
                    onProjectSelected = { project ->
                        selectedProject = project
                        currentScreen = Screen.PROJECT_DETAIL
                    },
                    onNavigateBack = { currentScreen = Screen.DASHBOARD }
                )
            }

            Screen.PROJECT_DETAIL -> {
                if (selectedProject != null) {
                    ProjectDetailScreen(
                        project = selectedProject!!,
                        checklists = activeProjectChecklists.value,
                        onUpdateChecklist = { updatedItem ->
                            scope.launch {
                                appDao.updateChecklistItem(updatedItem)
                            }
                        },
                        onArchiveProject = { updatedPrj ->
                            scope.launch {
                                appDao.updateProject(updatedPrj)
                                // Refresh selected reference
                                selectedProject = updatedPrj
                            }
                        },
                        onNavigateBack = { currentScreen = Screen.PROJECTS_LIST }
                    )
                } else {
                    currentScreen = Screen.PROJECTS_LIST
                }
            }

            Screen.TOOLS -> {
                ToolsScreen(
                    onNavigateBack = { currentScreen = Screen.DASHBOARD }
                )
            }

            Screen.SETTINGS -> {
                SettingsScreen(
                    onNavigateBack = { currentScreen = Screen.DASHBOARD }
                )
            }
        }
    }
}
