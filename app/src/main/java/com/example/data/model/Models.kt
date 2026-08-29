package com.example.data.model

data class UserProfile(
  val id: String = "user_me",
  val name: String = "Alex Rivera",
  val handle: String = "@alex_makes",
  val avatarEmoji: String = "⚡",
  val bio: String = "Passionate urban gardener, analogue synth enthusiast, and local board game host.",
  val location: String = "Downtown Arts District",
  val interests: List<String> = listOf("Board Games", "Urban Gardening", "Synth & Audio", "Photography", "Woodworking"),
  val reputationScore: Int = 98,
  val organizerRating: Float = 4.95f,
  val isVerifiedOrganizer: Boolean = true,
  val eventsHostedCount: Int = 24,
  val badges: List<String> = listOf("Verified Host", "Master Artisan", "Community Pillar", "Reliable Planner"),
  val encryptionKeyFingerprint: String = "SHA256:7f3a:89d2:e45b:19c0",
  val cloudSyncEnabled: Boolean = true,
  val offlineModeActive: Boolean = false,
  val darkModePreference: String = "SYSTEM" // SYSTEM, DARK, LIGHT
)

data class HobbyGroup(
  val id: String,
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
  val tags: List<String>,
  val rules: List<String>,
  val isMember: Boolean = false,
  val membershipRole: String = "MEMBER", // ORGANIZER, CO_HOST, MEMBER, NON_MEMBER
  val unreadMessagesCount: Int = 0
)

enum class EventRsvpStatus {
  GOING, MAYBE, NONE
}

enum class EventMeetingType {
  IN_PERSON, HYBRID, VIRTUAL_AUDIO_STAGE
}

data class EquipmentItem(
  val id: String,
  val name: String,
  val assignedTo: String = "",
  val isChecked: Boolean = false
)

data class HobbyEvent(
  val id: String,
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
  val userRsvp: EventRsvpStatus = EventRsvpStatus.NONE,
  val meetingType: EventMeetingType = EventMeetingType.IN_PERSON,
  val isLiveNow: Boolean = false,
  val equipmentList: List<EquipmentItem> = emptyList(),
  val coverIcon: String = "📅"
)

enum class ChatMediaType {
  NONE, IMAGE, AUDIO_NOTE, DOCUMENT, LOCATION_PIN
}

data class ChatMessage(
  val id: String,
  val conversationId: String, // Group ID or direct peer ID
  val senderId: String,
  val senderName: String,
  val senderAvatar: String,
  val text: String,
  val timestampFormatted: String,
  val isEncrypted: Boolean = true,
  val mediaType: ChatMediaType = ChatMediaType.NONE,
  val mediaCaption: String = "",
  val isFromMe: Boolean = false,
  val syncStatus: String = "SYNCED" // SYNCED, PENDING_OFFLINE, DELIVERED
)

data class MilestoneItem(
  val id: String,
  val groupId: String,
  val title: String,
  val dueDate: String,
  val assignedMember: String,
  val isCompleted: Boolean = false,
  val progressPercent: Int = 0
)

data class CollabDocument(
  val id: String,
  val groupId: String,
  val groupName: String,
  val title: String,
  val content: String,
  val lastModified: String,
  val activeEditors: List<String> = listOf("Alex Rivera (You)", "Maya Lin", "Devon Cole"),
  val version: Int = 1
)

data class AppNotification(
  val id: String,
  val title: String,
  val description: String,
  val timestampFormatted: String,
  val type: String, // EVENT_UPDATE, GROUP_INVITE, REPUTATION_BADGE, CHAT_MESSAGE, SYNC_STATUS
  val isRead: Boolean = false,
  val iconEmoji: String = "🔔"
)

enum class SyncState {
  ONLINE_SYNCED, OFFLINE_CACHED, SYNCING_IN_PROGRESS
}

enum class PostPrivacy {
  PUBLIC, FRIENDS, GROUP_ONLY
}

enum class ReactionType(val emoji: String, val label: String, val colorHex: Long) {
  LIKE("👍", "Like", 0xFF0866FF),
  LOVE("❤️", "Love", 0xFFFA383E),
  CARE("🥰", "Care", 0xFFF7B125),
  HAHA("😆", "Haha", 0xFFF7B125),
  WOW("😮", "Wow", 0xFFF79725),
  FIRE("🔥", "Fire", 0xFFFF5722)
}

data class FacebookComment(
  val id: String,
  val postId: String,
  val authorName: String,
  val authorAvatar: String,
  val text: String,
  val timestampFormatted: String,
  val likesCount: Int = 0,
  val isLiked: Boolean = false,
  val isVerified: Boolean = false
)

data class FacebookStory(
  val id: String,
  val authorName: String,
  val authorAvatar: String,
  val isMyStory: Boolean = false,
  val mediaEmoji: String = "✨",
  val caption: String = "",
  val timestamp: String = "1h",
  val gradientStart: Long = 0xFF0866FF,
  val gradientEnd: Long = 0xFF8B5CF6,
  val isSeen: Boolean = false
)

data class FacebookPost(
  val id: String,
  val authorId: String,
  val authorName: String,
  val authorAvatar: String,
  val authorBadge: String? = null,
  val isVerifiedAuthor: Boolean = false,
  val groupId: String? = null,
  val groupName: String? = null,
  val groupIcon: String? = null,
  val timestampFormatted: String,
  val privacy: PostPrivacy = PostPrivacy.PUBLIC,
  val feelingOrActivity: String? = null,
  val locationTag: String? = null,
  val content: String,
  val hashtags: List<String> = emptyList(),
  val mediaType: String = "NONE", // NONE, PHOTO_GRID, BLUEPRINT, EVENT_CARD, VIDEO_STAGE
  val mediaCaption: String = "",
  val mediaColorHex: Long = 0xFF0866FF,
  val mediaEmoji: String = "📸",
  val reactionsCount: Int = 0,
  val myReaction: ReactionType? = null,
  val topReactions: List<String> = listOf("👍", "❤️"),
  val commentsCount: Int = 0,
  val sharesCount: Int = 0,
  val attachedEventId: String? = null,
  val attachedEventTitle: String? = null,
  val attachedEventDate: String? = null,
  val isPinned: Boolean = false
)

data class FacebookFriend(
  val id: String,
  val name: String,
  val avatar: String,
  val mutualFriendsCount: Int,
  val mutualHobby: String,
  val distanceAway: String,
  val isFriend: Boolean = false,
  val hasPendingRequest: Boolean = false,
  val isOnline: Boolean = true,
  val isVerified: Boolean = false
)

