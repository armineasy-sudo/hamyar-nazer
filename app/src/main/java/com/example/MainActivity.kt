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
import com.example.data.ProjectEntity
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

enum class Screen {
    LANDING,
    LOGIN,
    DASHBOARD,
    PROJECTS_LIST,
    PROJECT_DETAIL,
    VISITS,
    TOOLS,
    SETTINGS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
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
    var currentScreen by remember { mutableStateOf(Screen.LANDING) }
    var engineerName by remember { mutableStateOf("") }
    var selectedProject by remember { mutableStateOf<ProjectEntity?>(null) }

    val database = remember { AppDatabase.getDatabase(context) }
    val appDao = remember { database.appDao() }
    val scope = rememberCoroutineScope()

    val projectsList by remember { appDao.getAllProjects() }.collectAsState(initial = emptyList())

    val visitsList by remember(selectedProject?.id) {
        if (selectedProject != null) {
            appDao.getVisitsForProject(selectedProject!!.id)
        } else {
            flowOf(emptyList())
        }
    }.collectAsState(initial = emptyList())

    val activeCount = projectsList.count { it.status == "ACTIVE" }
    val archivedCount = projectsList.count { it.status == "ARCHIVED" }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            Screen.LANDING -> {
                LandingScreen(onEnterApp = { currentScreen = Screen.LOGIN })
            }

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
                    onAddProject = { newProject ->
                        scope.launch { appDao.insertProject(newProject) }
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
                        onNavigateToVisits = { currentScreen = Screen.VISITS },
                        onArchiveProject = { updated ->
                            scope.launch { appDao.updateProject(updated) }
                            selectedProject = updated
                        },
                        onNavigateBack = { currentScreen = Screen.PROJECTS_LIST }
                    )
                } else {
                    currentScreen = Screen.PROJECTS_LIST
                }
            }

            Screen.VISITS -> {
                if (selectedProject != null) {
                    VisitFormScreen(
                        project = selectedProject!!,
                        visits = visitsList,
                        onSaveVisit = { visit ->
                            scope.launch {
                                if (visit.id == 0L) {
                                    appDao.insertVisit(visit)
                                } else {
                                    appDao.updateVisit(visit)
                                }
                            }
                        },
                        onDeleteVisit = { visit ->
                            scope.launch { appDao.deleteVisit(visit) }
                        },
                        onNavigateBack = { currentScreen = Screen.PROJECT_DETAIL }
                    )
                } else {
                    currentScreen = Screen.PROJECTS_LIST
                }
            }

            Screen.TOOLS -> {
                ToolsScreen(onNavigateBack = { currentScreen = Screen.DASHBOARD })
            }

            Screen.SETTINGS -> {
                SettingsScreen(onNavigateBack = { currentScreen = Screen.DASHBOARD })
            }
        }
    }
}
