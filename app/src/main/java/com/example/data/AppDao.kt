package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Projects
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Long): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    // Inspection Visits
    @Query("SELECT * FROM inspection_visits WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getVisitsForProject(projectId: Long): Flow<List<InspectionVisitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisit(visit: InspectionVisitEntity): Long

    @Update
    suspend fun updateVisit(visit: InspectionVisitEntity)

    @Delete
    suspend fun deleteVisit(visit: InspectionVisitEntity)

    // Project Checklists
    @Query("SELECT * FROM checklist_items WHERE projectId = :projectId ORDER BY id ASC")
    fun getChecklistsForProject(projectId: Long): Flow<List<ChecklistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklists(checklists: List<ChecklistEntity>)

    @Update
    suspend fun updateChecklist(checklist: ChecklistEntity)

    // Checklist Items
    @Query("SELECT * FROM checklist_items ORDER BY id ASC")
    fun getAllChecklistItems(): Flow<List<ChecklistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItems(items: List<ChecklistItemEntity>)

    // Inspection Item Results
    @Query("SELECT * FROM inspection_item_results WHERE visitId = :visitId")
    fun getResultsForVisit(visitId: Long): Flow<List<InspectionItemResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateResult(result: InspectionItemResultEntity)

    // Report Versions
    @Query("SELECT * FROM report_versions WHERE visitId = :visitId ORDER BY versionNumber DESC")
    fun getReportVersions(visitId: Long): Flow<List<ReportVersionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReportVersion(report: ReportVersionEntity): Long

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)
}
