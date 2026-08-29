package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.EventEntity
import com.example.data.local.entity.GroupEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.MilestoneEntity
import com.example.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
  @Query("SELECT * FROM groups")
  fun getAllGroups(): Flow<List<GroupEntity>>

  @Query("SELECT * FROM groups WHERE id = :id")
  suspend fun getGroupById(id: String): GroupEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(groups: List<GroupEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGroup(group: GroupEntity)

  @Update
  suspend fun updateGroup(group: GroupEntity)

  @Query("UPDATE groups SET isMember = :isMember, membersCount = membersCount + :delta WHERE id = :groupId")
  suspend fun updateMembership(groupId: String, isMember: Boolean, delta: Int)
}

@Dao
interface EventDao {
  @Query("SELECT * FROM events")
  fun getAllEvents(): Flow<List<EventEntity>>

  @Query("SELECT * FROM events WHERE id = :id")
  suspend fun getEventById(id: String): EventEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(events: List<EventEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvent(event: EventEntity)

  @Query("UPDATE events SET userRsvp = :status, currentRsvpCount = :rsvpCount WHERE id = :eventId")
  suspend fun updateRsvp(eventId: String, status: String, rsvpCount: Int)

  @Query("UPDATE events SET equipmentRaw = :equipmentRaw WHERE id = :eventId")
  suspend fun updateEquipment(eventId: String, equipmentRaw: String)
}

@Dao
interface MessageDao {
  @Query("SELECT * FROM messages WHERE conversationId = :convId ORDER BY id ASC")
  fun getMessagesForConversation(convId: String): Flow<List<MessageEntity>>

  @Query("SELECT * FROM messages ORDER BY id DESC")
  fun getAllMessages(): Flow<List<MessageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: MessageEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(messages: List<MessageEntity>)
}

@Dao
interface DocumentDao {
  @Query("SELECT * FROM collab_documents")
  fun getAllDocuments(): Flow<List<DocumentEntity>>

  @Query("SELECT * FROM collab_documents WHERE id = :docId")
  suspend fun getDocumentById(docId: String): DocumentEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDocument(doc: DocumentEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(docs: List<DocumentEntity>)

  @Query("UPDATE collab_documents SET content = :content, lastModified = :lastModified, version = version + 1 WHERE id = :docId")
  suspend fun updateDocumentContent(docId: String, content: String, lastModified: String)
}

@Dao
interface MilestoneDao {
  @Query("SELECT * FROM milestones WHERE groupId = :groupId")
  fun getMilestonesForGroup(groupId: String): Flow<List<MilestoneEntity>>

  @Query("SELECT * FROM milestones")
  fun getAllMilestones(): Flow<List<MilestoneEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(milestones: List<MilestoneEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMilestone(milestone: MilestoneEntity)

  @Query("UPDATE milestones SET isCompleted = :isCompleted, progressPercent = :progress WHERE id = :id")
  suspend fun updateMilestoneStatus(id: String, isCompleted: Boolean, progress: Int)
}

@Dao
interface NotificationDao {
  @Query("SELECT * FROM notifications ORDER BY id DESC")
  fun getAllNotifications(): Flow<List<NotificationEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(notifications: List<NotificationEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotification(notification: NotificationEntity)

  @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
  suspend fun markAsRead(id: String)

  @Query("UPDATE notifications SET isRead = 1")
  suspend fun markAllAsRead()
}
