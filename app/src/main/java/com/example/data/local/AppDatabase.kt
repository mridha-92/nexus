package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.DocumentDao
import com.example.data.local.dao.EventDao
import com.example.data.local.dao.GroupDao
import com.example.data.local.dao.MessageDao
import com.example.data.local.dao.MilestoneDao
import com.example.data.local.dao.NotificationDao
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.EventEntity
import com.example.data.local.entity.GroupEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.MilestoneEntity
import com.example.data.local.entity.NotificationEntity

@Database(
  entities = [
    GroupEntity::class,
    EventEntity::class,
    MessageEntity::class,
    DocumentEntity::class,
    MilestoneEntity::class,
    NotificationEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun groupDao(): GroupDao
  abstract fun eventDao(): EventDao
  abstract fun messageDao(): MessageDao
  abstract fun documentDao(): DocumentDao
  abstract fun milestoneDao(): MilestoneDao
  abstract fun notificationDao(): NotificationDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "hobby_circle.db"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
