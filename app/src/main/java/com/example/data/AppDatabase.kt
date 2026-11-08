package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

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

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inspector_database"
                )
                // Migration امن بعداً اضافه می‌شود. حالاً برای توسعه از destructive استفاده نمی‌شود.
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
