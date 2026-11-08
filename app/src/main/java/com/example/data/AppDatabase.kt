package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ProjectEntity::class,
        ChecklistEntity::class,
        InspectionVisitEntity::class,
        ChecklistItemEntity::class,
        InspectionItemResultEntity::class,
        ReportVersionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Migration امن از نسخه ۱ به ۲
         * جدول‌های جدید اضافه می‌شوند و داده‌های قبلی حفظ می‌شوند.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // جدول بازدید‌ها
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS inspection_visits (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        projectId INTEGER NOT NULL,
                        visitDateShamsi TEXT NOT NULL,
                        visitTime TEXT,
                        executionStage TEXT NOT NULL,
                        notes TEXT,
                        status TEXT NOT NULL DEFAULT 'COMPLETED',
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY(projectId) REFERENCES projects(id) ON DELETE CASCADE
                    )
                """.trimIndent())

                // جدول چک‌لیست
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS checklist_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        projectId INTEGER NOT NULL,
                        category TEXT NOT NULL,
                        title TEXT NOT NULL,
                        status TEXT NOT NULL DEFAULT 'NOT_COMPLETED',
                        priority TEXT NOT NULL DEFAULT 'MEDIUM',
                        regulationReference TEXT NOT NULL DEFAULT '',
                        inspectorNotes TEXT NOT NULL DEFAULT '',
                        photoPath TEXT,
                        FOREIGN KEY(projectId) REFERENCES projects(id) ON DELETE CASCADE
                    )
                """.trimIndent())

                // جدول نتایج بندها
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS inspection_item_results (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        visitId INTEGER NOT NULL,
                        checklistItemId INTEGER NOT NULL,
                        status TEXT NOT NULL,
                        notes TEXT,
                        FOREIGN KEY(visitId) REFERENCES inspection_visits(id) ON DELETE CASCADE
                    )
                """.trimIndent())

                // جدول نسخه‌های گزارش
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS report_versions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        visitId INTEGER NOT NULL,
                        versionNumber INTEGER NOT NULL,
                        content TEXT NOT NULL,
                        tone TEXT NOT NULL DEFAULT 'NORMAL',
                        includeGas INTEGER NOT NULL DEFAULT 0,
                        municipalityRegistrationNumber TEXT,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY(visitId) REFERENCES inspection_visits(id) ON DELETE CASCADE
                    )
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inspector_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
