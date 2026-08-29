package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(
  @PrimaryKey val id: String,
  val name: String,
  val category: String,
  val description: String,
  val isPrivate: Boolean,
  val membersCount: Int,
  val organizerId: String,
  val organizerName: String,
  val organizerReputation: Int,
  val isVerifiedOrganizer: Boolean,
  val locationName: String,
  val latitude: Double,
  val longitude: Double,
  val iconEmoji: String,
  val coverColorHex: Long,
  val tagsJoined: String,
  val rulesJoined: String,
  val isMember: Boolean,
  val membershipRole: String
)

@Entity(tableName = "events")
data class EventEntity(
  @PrimaryKey val id: String,
  val groupId: String,
  val groupName: String,
  val title: String,
  val category: String,
  val description: String,
  val dateFormatted: String,
  val timeFormatted: String,
  val locationName: String,
  val latitude: Double,
  val longitude: Double,
  val organizerName: String,
  val organizerReputation: Int,
  val isVerifiedOrganizer: Boolean,
  val maxAttendees: Int,
  val currentRsvpCount: Int,
  val userRsvp: String, // GOING, MAYBE, NONE
  val meetingType: String,
  val isLiveNow: Boolean,
  val equipmentRaw: String,
  val coverIcon: String
)

@Entity(tableName = "messages")
data class MessageEntity(
  @PrimaryKey val id: String,
  val conversationId: String,
  val senderId: String,
  val senderName: String,
  val senderAvatar: String,
  val text: String,
  val timestampFormatted: String,
  val isEncrypted: Boolean,
  val mediaType: String,
  val mediaCaption: String,
  val isFromMe: Boolean,
  val syncStatus: String
)

@Entity(tableName = "collab_documents")
data class DocumentEntity(
  @PrimaryKey val id: String,
  val groupId: String,
  val groupName: String,
  val title: String,
  val content: String,
  val lastModified: String,
  val activeEditorsJoined: String,
  val version: Int
)

@Entity(tableName = "milestones")
data class MilestoneEntity(
  @PrimaryKey val id: String,
  val groupId: String,
  val title: String,
  val dueDate: String,
  val assignedMember: String,
  val isCompleted: Boolean,
  val progressPercent: Int
)

@Entity(tableName = "notifications")
data class NotificationEntity(
  @PrimaryKey val id: String,
  val title: String,
  val description: String,
  val timestampFormatted: String,
  val type: String,
  val isRead: Boolean,
  val iconEmoji: String
)
