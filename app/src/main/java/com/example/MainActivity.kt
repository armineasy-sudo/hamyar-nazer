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
import com.example.data.*
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
    CHECKLIST,
    REPORT_VERSIONS,
    USER_PROFILE,
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
    var selectedVisitId by remember { mutableStateOf(0L) }
    var hasSeeded by remember { mutableStateOf(false) }

    val database = remember { AppDatabase.getDatabase(context) }
    val appDao = remember { database.appDao() }
    val scope = rememberCoroutineScope()

    val projectsList by remember { appDao.getAllProjects() }.collectAsState(initial = emptyList())

    LaunchedEffect(projectsList) {
        if (!hasSeeded && projectsList.isEmpty()) {
            scope.launch {
                SampleDataSeeder.getSampleProjects().forEach { project ->
                    val projectId = appDao.insertProject(project)
                    val sampleVisits = SampleDataSeeder.getSampleVisits(projectId, project.name)
                    sampleVisits.forEach { visit -> appDao.insertVisit(visit) }
                    val checklists = ChecklistSeeder.getDefaultChecklists(projectId)
                    appDao.insertChecklists(checklists)
                }
            }
            hasSeeded = true
        }
    }

    val visitsList by remember(selectedProject?.id) {
        if (selectedProject != null) appDao.getVisitsForProject(selectedProject!!.id) else flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    val checklistsList by remember(selectedProject?.id) {
        if (selectedProject != null) appDao.getChecklistsForProject(selectedProject!!.id) else flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    val reportVersions by remember(selectedVisitId) {
        if (selectedVisitId > 0) appDao.getReportVersions(selectedVisitId) else flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    val userProfile by remember { appDao.getUserProfile() }.collectAsState(initial = null)

    val activeCount = projectsList.count { it.status == "ACTIVE" }
    val archivedCount = projectsList.count { it.status == "ARCHIVED" }

    val syncStatusText = SyncManager.getStatusText(null)

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            Screen.LANDING -> LandingScreen(onEnterApp = { currentScreen = Screen.LOGIN })

            Screen.LOGIN -> LoginScreen(onLoginSuccess = { name ->
                engineerName = name
                currentScreen = Screen.DASHBOARD
            })

            Screen.DASHBOARD -> DashboardScreen(
                engineerName = engineerName,
                onNavigateToProjects = { currentScreen = Screen.PROJECTS_LIST },
                onNavigateToTools = { currentScreen = Screen.TOOLS },
                onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                projectCount = activeCount,
                archivedCount = archivedCount,
                syncStatusText = syncStatusText
            )

            Screen.PROJECTS_LIST -> ProjectListScreen(
                projects = projectsList,
                onAddProject = { newProject ->
                    scope.launch {
                        val id = appDao.insertProject(newProject)
                        val checklists = ChecklistSeeder.getDefaultChecklists(id)
                        appDao.insertChecklists(checklists)
                    }
                },
                onUpdateProject = { updated ->
                    scope.launch { appDao.updateProject(updated) }
                },
                onDeleteProject = { project ->
                    scope.launch { appDao.deleteProject(project) }
                },
                onProjectSelected = { project ->
                    selectedProject = project
                    currentScreen = Screen.PROJECT_DETAIL
                },
                onNavigateBack = { currentScreen = Screen.DASHBOARD }
            )

            Screen.PROJECT_DETAIL -> {
                if (selectedProject != null) {
                    ProjectDetailScreen(
                        project = selectedProject!!,
                        onNavigateToVisits = { currentScreen = Screen.VISITS },
                        onNavigateToChecklist = { currentScreen = Screen.CHECKLIST },
                        onArchiveProject = { updated ->
                            scope.launch { appDao.updateProject(updated.copy(updatedAt = System.currentTimeMillis())) }
                            selectedProject = updated.copy(updatedAt = System.currentTimeMillis())
                        },
                        onNavigateBack = { currentScreen = Screen.PROJECTS_LIST }
                    )
                } else currentScreen = Screen.PROJECTS_LIST
            }

            Screen.CHECKLIST -> {
                if (selectedProject != null) {
                    ChecklistScreen(
                        project = selectedProject!!,
                        checklists = checklistsList,
                        onUpdateChecklist = { updated ->
                            scope.launch { appDao.updateChecklist(updated) }
                        },
                        onNavigateBack = { currentScreen = Screen.PROJECT_DETAIL }
                    )
                } else currentScreen = Screen.PROJECTS_LIST
            }

            Screen.REPORT_VERSIONS -> {
                ReportVersionsScreen(
                    versions = reportVersions,
                    onNavigateBack = { currentScreen = Screen.VISITS }
                )
            }

            Screen.USER_PROFILE -> {
                UserProfileScreen(
                    profile = userProfile ?: UserProfile(),
                    onSaveProfile = { profile ->
                        scope.launch { appDao.insertOrUpdateProfile(profile) }
                    },
                    onNavigateBack = { currentScreen = Screen.SETTINGS }
                )
            }

            Screen.VISITS -> {
                if (selectedProject != null) {
                    VisitFormScreen(
                        project = selectedProject!!,
                        visits = visitsList,
                        checklists = checklistsList,
                        onSaveVisit = { visit ->
                            scope.launch {
                                if (visit.id == 0L) appDao.insertVisit(visit) else appDao.updateVisit(visit)
                                val updatedProject = selectedProject!!.copy(updatedAt = System.currentTimeMillis())
                                appDao.updateProject(updatedProject)
                                selectedProject = updatedProject
                            }
                        },
                        onDeleteVisit = { visit -> scope.launch { appDao.deleteVisit(visit) } },
                        onSaveReportVersion = { version ->
                            scope.launch { appDao.insertReportVersion(version) }
                        },
                        onNavigateToReportVersions = {
                            selectedVisitId = visitsList.firstOrNull()?.id ?: 0L
                            currentScreen = Screen.REPORT_VERSIONS
                        },
                        onNavigateBack = { currentScreen = Screen.PROJECT_DETAIL }
                    )
                } else currentScreen = Screen.PROJECTS_LIST
            }

            Screen.TOOLS -> ToolsScreen(onNavigateBack = { currentScreen = Screen.DASHBOARD })

            Screen.SETTINGS -> SettingsScreen(
                onNavigateBack = { currentScreen = Screen.DASHBOARD },
                onNavigateToProfile = { currentScreen = Screen.USER_PROFILE }
            )
        }
    }
}
