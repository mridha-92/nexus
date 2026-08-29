package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AppNotification
import com.example.data.model.ChatMediaType
import com.example.data.model.ChatMessage
import com.example.data.model.CollabDocument
import com.example.data.model.EquipmentItem
import com.example.data.model.EventMeetingType
import com.example.data.model.EventRsvpStatus
import com.example.data.model.FacebookComment
import com.example.data.model.FacebookFriend
import com.example.data.model.FacebookPost
import com.example.data.model.FacebookStory
import com.example.data.model.HobbyEvent
import com.example.data.model.HobbyGroup
import com.example.data.model.MilestoneItem
import com.example.data.model.PostPrivacy
import com.example.data.model.ReactionType
import com.example.data.model.SyncState
import com.example.data.model.UserProfile
import com.example.data.repository.HobbyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DiscoveryUiState(
  val selectedCategory: String = "All",
  val searchKeyword: String = "",
  val searchRadiusKm: Float = 15f,
  val selectedPinGroup: HobbyGroup? = null,
  val selectedPinEvent: HobbyEvent? = null,
  val mapCenterLat: Double = 37.7749,
  val mapCenterLng: Double = -122.4194
)

data class VideoLoungeState(
  val isInCall: Boolean = false,
  val isMuted: Boolean = false,
  val isCameraOn: Boolean = true,
  val isScreenSharing: Boolean = false,
  val activeReactions: List<String> = emptyList()
)

class HobbyViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: HobbyRepository

  init {
    val database = AppDatabase.getDatabase(application)
    repository = HobbyRepository(database)
  }

  // Facebook Feeds, Stories, and Friends
  val posts: StateFlow<List<FacebookPost>> = repository.posts
  val stories: StateFlow<List<FacebookStory>> = repository.stories
  val comments: StateFlow<Map<String, List<FacebookComment>>> = repository.comments
  val friends: StateFlow<List<FacebookFriend>> = repository.friends

  // Active Story & Comments Sheet State
  private val _activeStory = MutableStateFlow<FacebookStory?>(null)
  val activeStory: StateFlow<FacebookStory?> = _activeStory.asStateFlow()

  private val _activeCommentsPost = MutableStateFlow<FacebookPost?>(null)
  val activeCommentsPost: StateFlow<FacebookPost?> = _activeCommentsPost.asStateFlow()

  private val _isCreatePostOpen = MutableStateFlow(false)
  val isCreatePostOpen: StateFlow<Boolean> = _isCreatePostOpen.asStateFlow()

  val groups: StateFlow<List<HobbyGroup>> = repository.allGroups
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val events: StateFlow<List<HobbyEvent>> = repository.allEvents
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allMessages: StateFlow<List<ChatMessage>> = repository.allMessages
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val documents: StateFlow<List<CollabDocument>> = repository.allDocuments
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val milestones: StateFlow<List<MilestoneItem>> = repository.allMilestones
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val notifications: StateFlow<List<AppNotification>> = repository.allNotifications
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val userProfile: StateFlow<UserProfile> = repository.userProfile
  val syncState: StateFlow<SyncState> = repository.syncState

  // Navigation and UI state
  private val _discoveryState = MutableStateFlow(DiscoveryUiState())
  val discoveryState: StateFlow<DiscoveryUiState> = _discoveryState.asStateFlow()

  private val _activeConversationId = MutableStateFlow<String?>("group_1")
  val activeConversationId: StateFlow<String?> = _activeConversationId.asStateFlow()

  private val _activeDocumentId = MutableStateFlow<String?>("doc_1")
  val activeDocumentId: StateFlow<String?> = _activeDocumentId.asStateFlow()

  private val _videoLoungeState = MutableStateFlow(VideoLoungeState())
  val videoLoungeState: StateFlow<VideoLoungeState> = _videoLoungeState.asStateFlow()

  private val _onboardingCompleted = MutableStateFlow(true)
  val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

  val unreadNotificationsCount: StateFlow<Int> = notifications.combine(MutableStateFlow(Unit)) { list, _ ->
    list.count { !it.isRead }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  // --- Facebook Social Feed Actions ---
  fun reactToPost(postId: String, reaction: ReactionType) {
    repository.reactToPost(postId, reaction)
  }

  fun createFacebookPost(
    content: String,
    groupId: String? = null,
    groupName: String? = null,
    feelingOrActivity: String? = null,
    locationTag: String? = null,
    mediaType: String = "NONE",
    mediaCaption: String = "",
    attachedEventId: String? = null,
    attachedEventTitle: String? = null,
    privacy: PostPrivacy = PostPrivacy.PUBLIC
  ) {
    repository.createFacebookPost(
      content = content,
      groupId = groupId,
      groupName = groupName,
      feelingOrActivity = feelingOrActivity,
      locationTag = locationTag,
      mediaType = mediaType,
      mediaCaption = mediaCaption,
      attachedEventId = attachedEventId,
      attachedEventTitle = attachedEventTitle,
      privacy = privacy
    )
    _isCreatePostOpen.value = false
  }

  fun setCreatePostOpen(isOpen: Boolean) {
    _isCreatePostOpen.value = isOpen
  }

  fun openComments(post: FacebookPost) {
    _activeCommentsPost.value = post
  }

  fun closeComments() {
    _activeCommentsPost.value = null
  }

  fun addComment(postId: String, text: String) {
    if (text.isBlank()) return
    repository.addComment(postId, text)
  }

  fun toggleLikeComment(postId: String, commentId: String) {
    repository.toggleLikeComment(postId, commentId)
  }

  fun sharePost(postId: String) {
    repository.sharePost(postId)
  }

  fun openStory(story: FacebookStory) {
    _activeStory.value = story
    repository.markStorySeen(story.id)
  }

  fun closeStory() {
    _activeStory.value = null
  }

  fun toggleFriend(friendId: String) {
    repository.toggleFriend(friendId)
  }

  fun acceptFriendRequest(friendId: String) {
    repository.acceptFriendRequest(friendId)
  }


  // --- Discovery Actions ---
  fun setDiscoveryCategory(category: String) {
    _discoveryState.value = _discoveryState.value.copy(selectedCategory = category)
  }

  fun setDiscoverySearch(query: String) {
    _discoveryState.value = _discoveryState.value.copy(searchKeyword = query)
  }

  fun setDiscoveryRadius(radiusKm: Float) {
    _discoveryState.value = _discoveryState.value.copy(searchRadiusKm = radiusKm)
  }

  fun selectMapPinGroup(group: HobbyGroup?) {
    _discoveryState.value = _discoveryState.value.copy(selectedPinGroup = group, selectedPinEvent = null)
  }

  fun selectMapPinEvent(event: HobbyEvent?) {
    _discoveryState.value = _discoveryState.value.copy(selectedPinEvent = event, selectedPinGroup = null)
  }

  // --- Group Actions ---
  fun toggleGroupMembership(groupId: String, currentStatus: Boolean) {
    viewModelScope.launch {
      repository.toggleGroupMembership(groupId, currentStatus)
    }
  }

  fun createGroup(
    name: String,
    category: String,
    description: String,
    isPrivate: Boolean,
    locationName: String,
    tags: List<String>,
    rules: List<String>,
    iconEmoji: String
  ) {
    viewModelScope.launch {
      repository.createGroup(
        name = name,
        category = category,
        description = description,
        isPrivate = isPrivate,
        locationName = locationName,
        latitude = 37.7749 + (Math.random() - 0.5) * 0.04,
        longitude = -122.4194 + (Math.random() - 0.5) * 0.04,
        iconEmoji = iconEmoji,
        tags = tags,
        rules = rules
      )
    }
  }

  // --- Event Actions ---
  fun updateEventRsvp(eventId: String, newStatus: EventRsvpStatus) {
    viewModelScope.launch {
      repository.updateEventRsvp(eventId, newStatus)
    }
  }

  fun toggleEquipmentItem(eventId: String, itemId: String) {
    viewModelScope.launch {
      repository.toggleEquipmentItem(eventId, itemId)
    }
  }

  fun createEvent(
    groupId: String,
    title: String,
    category: String,
    description: String,
    dateFormatted: String,
    timeFormatted: String,
    locationName: String,
    maxAttendees: Int,
    meetingType: EventMeetingType,
    equipmentList: List<EquipmentItem>
  ) {
    viewModelScope.launch {
      repository.createEvent(
        groupId = groupId,
        title = title,
        category = category,
        description = description,
        dateFormatted = dateFormatted,
        timeFormatted = timeFormatted,
        locationName = locationName,
        latitude = 37.7749 + (Math.random() - 0.5) * 0.04,
        longitude = -122.4194 + (Math.random() - 0.5) * 0.04,
        maxAttendees = maxAttendees,
        meetingType = meetingType,
        equipmentList = equipmentList
      )
    }
  }

  // --- Messaging Actions ---
  fun setActiveConversation(convId: String) {
    _activeConversationId.value = convId
  }

  fun sendMessage(convId: String, text: String, mediaType: ChatMediaType = ChatMediaType.NONE, mediaCaption: String = "") {
    if (text.isBlank() && mediaType == ChatMediaType.NONE) return
    viewModelScope.launch {
      repository.sendMessage(convId, text, mediaType, mediaCaption)
    }
  }

  // --- Document & Collaboration Actions ---
  fun setActiveDocument(docId: String) {
    _activeDocumentId.value = docId
  }

  fun updateDocument(docId: String, content: String) {
    viewModelScope.launch {
      repository.updateDocumentContent(docId, content)
    }
  }

  fun createDocument(groupId: String, groupName: String, title: String, content: String) {
    viewModelScope.launch {
      repository.createDocument(groupId, groupName, title, content)
    }
  }

  fun toggleMilestone(id: String, isCompleted: Boolean) {
    viewModelScope.launch {
      repository.toggleMilestone(id, isCompleted)
    }
  }

  fun createMilestone(groupId: String, title: String, dueDate: String, assignedMember: String) {
    viewModelScope.launch {
      repository.createMilestone(groupId, title, dueDate, assignedMember)
    }
  }

  // --- Video Lounge Actions ---
  fun toggleCallStatus() {
    val current = _videoLoungeState.value
    _videoLoungeState.value = current.copy(isInCall = !current.isInCall)
  }

  fun toggleMute() {
    val current = _videoLoungeState.value
    _videoLoungeState.value = current.copy(isMuted = !current.isMuted)
  }

  fun toggleCamera() {
    val current = _videoLoungeState.value
    _videoLoungeState.value = current.copy(isCameraOn = !current.isCameraOn)
  }

  fun toggleScreenShare() {
    val current = _videoLoungeState.value
    _videoLoungeState.value = current.copy(isScreenSharing = !current.isScreenSharing)
  }

  fun sendReaction(emoji: String) {
    val current = _videoLoungeState.value
    _videoLoungeState.value = current.copy(activeReactions = (current.activeReactions + emoji).takeLast(6))
  }

  // --- Notifications ---
  fun markNotificationRead(id: String) {
    viewModelScope.launch {
      repository.markNotificationRead(id)
    }
  }

  fun markAllNotificationsRead() {
    viewModelScope.launch {
      repository.markAllNotificationsRead()
    }
  }

  // --- Settings & Sync ---
  fun setDarkModePreference(mode: String) {
    repository.updateDarkMode(mode)
  }

  fun toggleOfflineMode() {
    repository.toggleOfflineMode()
  }

  fun triggerManualSync() {
    viewModelScope.launch {
      repository.triggerManualSync()
    }
  }

  fun endorseOrganizer(points: Int = 2) {
    repository.endorseOrganizer(points)
  }

  fun setOnboardingCompleted(completed: Boolean) {
    _onboardingCompleted.value = completed
  }
}
