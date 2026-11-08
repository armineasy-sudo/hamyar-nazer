package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * وضعیت همگام‌سازی آفلاین
 * برای نسخه فعلی، داده‌ها محلی ذخیره می‌شوند و وضعیت همگام‌سازی نمایش داده می‌شود.
 */
@Entity(tableName = "sync_status")
data class SyncStatus(
    @PrimaryKey val id: Int = 1,
    val lastSyncTime: Long = 0L,
    val pendingChanges: Int = 0,
    val isOnline: Boolean = true
)

object SyncManager {
    fun getStatusText(status: SyncStatus?): String {
        if (status == null) return "آفلاین"
        return when {
            status.pendingChanges > 0 -> "${status.pendingChanges} تغییر در انتظار همگام‌سازی"
            status.lastSyncTime > 0 -> "همگام‌سازی شد"
            else -> "آفلاین"
        }
    }
}
