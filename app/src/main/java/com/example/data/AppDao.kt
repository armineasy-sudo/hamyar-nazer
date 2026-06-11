package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Projects Queries
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Long): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    // Checklists Queries
    @Query("SELECT * FROM checklist_items WHERE projectId = :projectId ORDER BY id ASC")
    fun getChecklistsForProject(projectId: Long): Flow<List<ChecklistEntity>>

    @Query("SELECT * FROM checklist_items WHERE projectId = :projectId ORDER BY id ASC")
    suspend fun getChecklistsForProjectSync(projectId: Long): List<ChecklistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklists(items: List<ChecklistEntity>)

    @Update
    suspend fun updateChecklistItem(item: ChecklistEntity)
}
