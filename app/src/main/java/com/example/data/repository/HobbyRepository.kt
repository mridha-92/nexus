package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.EventEntity
import com.example.data.local.entity.GroupEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.MilestoneEntity
import com.example.data.local.entity.NotificationEntity
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HobbyRepository(private val database: AppDatabase) {

  private val coroutineScope = CoroutineScope(Dispatchers.IO)

  private val _syncState = MutableStateFlow(SyncState.ONLINE_SYNCED)
  val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

  private val _userProfile = MutableStateFlow(UserProfile())
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  // Facebook Social Feeds & Interactive State
  private val _posts = MutableStateFlow<List<FacebookPost>>(emptyList())
  val posts: StateFlow<List<FacebookPost>> = _posts.asStateFlow()

  private val _stories = MutableStateFlow<List<FacebookStory>>(emptyList())
  val stories: StateFlow<List<FacebookStory>> = _stories.asStateFlow()

  private val _comments = MutableStateFlow<Map<String, List<FacebookComment>>>(emptyMap())
  val comments: StateFlow<Map<String, List<FacebookComment>>> = _comments.asStateFlow()

  private val _friends = MutableStateFlow<List<FacebookFriend>>(emptyList())
  val friends: StateFlow<List<FacebookFriend>> = _friends.asStateFlow()

  init {
    seedFacebookSocialData()
    coroutineScope.launch {
      seedInitialDataIfNeeded()
    }
  }

  private fun seedFacebookSocialData() {
    _stories.value = listOf(
      FacebookStory(
        id = "story_me",
        authorName = "Your Story",
        authorAvatar = "⚡",
        isMyStory = true,
        mediaEmoji = "➕",
        caption = "Add a photo, update, or live stage to your story",
        timestamp = "Now",
        gradientStart = 0xFF0866FF,
        gradientEnd = 0xFF00C6FF,
        isSeen = true
      ),
      FacebookStory(
        id = "story_1",
        authorName = "Elena Rostova",
        authorAvatar = "🌱",
        isMyStory = false,
        mediaEmoji = "🌿",
        caption = "First heirloom Cherokee Purple tomato blossoms blooming in the rooftop hydroponic chamber!",
        timestamp = "35m",
        gradientStart = 0xFF10B981,
        gradientEnd = 0xFF059669,
        isSeen = false
      ),
      FacebookStory(
        id = "story_2",
        authorName = "Marcus Thorne",
        authorAvatar = "🎲",
        isMyStory = false,
        mediaEmoji = "🏆",
        caption = "Unboxing the deluxe custom laser-cut player boards for tomorrow's Dune Imperium match!",
        timestamp = "2h",
        gradientStart = 0xFF4F46E5,
        gradientEnd = 0xFF7C3AED,
        isSeen = false
      ),
      FacebookStory(
        id = "story_3",
        authorName = "Nina Patel",
        authorAvatar = "🎛️",
        isMyStory = false,
        mediaEmoji = "🎹",
        caption = "New stereo tape delay patch on the Eurorack modular rig. Join our live lounge stage!",
        timestamp = "4h",
        gradientStart = 0xFF8B5CF6,
        gradientEnd = 0xFFEC4899,
        isSeen = false
      ),
      FacebookStory(
        id = "story_4",
        authorName = "Dr. David Kim",
        authorAvatar = "🔭",
        isMyStory = false,
        mediaEmoji = "🌌",
        caption = "Stacked 45 frames of the Andromeda Galaxy from Twin Peaks point tonight.",
        timestamp = "6h",
        gradientStart = 0xFF1E1B4B,
        gradientEnd = 0xFF3B82F6,
        isSeen = false
      )
    )

    _posts.value = listOf(
      FacebookPost(
        id = "post_1",
        authorId = "org_elena",
        authorName = "Elena Rostova",
        authorAvatar = "🌱",
        authorBadge = "Master Gardener",
        isVerifiedAuthor = true,
        groupId = "group_2",
        groupName = "GreenThumb Urban Gardeners",
        groupIcon = "🌱",
        timestampFormatted = "15 mins ago",
        privacy = PostPrivacy.PUBLIC,
        feelingOrActivity = "🌱 feeling excited",
        locationTag = "Riverside Community Allotment",
        content = "We have officially prepared 300+ heirloom tomato and sweet basil seedling packets for this Saturday's annual Seed Swap & Soil Workshop! Bring your saved seeds or just come learn soil pH balancing. All neighbors are welcome! 🍅🌿",
        hashtags = listOf("#UrbanGardening", "#HeirloomSeeds", "#GreenThumb", "#CommunityPlot"),
        mediaType = "PHOTO_GRID",
        mediaCaption = "Heirloom Seedlings & Organic Fertilizer Mix Ready for Distribution",
        mediaColorHex = 0xFF10B981,
        mediaEmoji = "🌱",
        reactionsCount = 38,
        myReaction = null,
        topReactions = listOf("❤️", "👍", "🌱"),
        commentsCount = 8,
        sharesCount = 5,
        attachedEventId = "event_2",
        attachedEventTitle = "Heirloom Seed Swap & Soil pH Testing Workshop",
        attachedEventDate = "Saturday, 10:00 AM • 22 Attending",
        isPinned = true
      ),
      FacebookPost(
        id = "post_2",
        authorId = "org_marcus",
        authorName = "Marcus Thorne",
        authorAvatar = "🎲",
        authorBadge = "Guild Organizer",
        isVerifiedAuthor = true,
        groupId = "group_1",
        groupName = "Metropolis Board Game Guild",
        groupIcon = "🎲",
        timestampFormatted = "2 hours ago",
        privacy = PostPrivacy.PUBLIC,
        feelingOrActivity = "🎯 playing tabletop games",
        locationTag = "The Boardroom Cafe, Downtown",
        content = "Final tournament bracket locked in for tomorrow's Heavy Euro Night! We are playing Dune Imperium: Uprising and Brass Birmingham Deluxe. Tables 4, 5, and 6 are reserved. Alex Rivera is bringing the deluxe metal coin set!",
        hashtags = listOf("#BoardGames", "#Eurogames", "#Tabletop", "#DuneImperium"),
        mediaType = "EVENT_CARD",
        mediaCaption = "Tournament Setup: Round 1 Matches",
        mediaColorHex = 0xFF4F46E5,
        mediaEmoji = "🎲",
        reactionsCount = 52,
        myReaction = ReactionType.LIKE,
        topReactions = listOf("👍", "🔥", "❤️"),
        commentsCount = 14,
        sharesCount = 9,
        attachedEventId = "event_1",
        attachedEventTitle = "Heavy Euro Night: Dune Imperium & Brass Birmingham",
        attachedEventDate = "Tomorrow, 6:30 PM • 9 Attending"
      ),
      FacebookPost(
        id = "post_3",
        authorId = "org_nina",
        authorName = "Nina Patel",
        authorAvatar = "🎛️",
        authorBadge = "Sound Engineer",
        isVerifiedAuthor = true,
        groupId = "group_4",
        groupName = "Analogue Synth & Modular Audio Jam",
        groupIcon = "🎛️",
        timestampFormatted = "4 hours ago",
        privacy = PostPrivacy.PUBLIC,
        feelingOrActivity = "🎧 listening to ambient soundscapes",
        locationTag = "Underground Sound Lab, Unit 3B",
        content = "Live audio lounge session starting in 30 minutes! We're hooking up Pamela's PRO Workout to a tape loop bank for generative generative drone soundscapes. Tune in live or join the spatial video lounge to jam together.",
        hashtags = listOf("#Eurorack", "#ModularSynth", "#AmbientJam", "#TapeLoops"),
        mediaType = "VIDEO_STAGE",
        mediaCaption = "Live Studio Stage: 4 Jam Participants On-Air",
        mediaColorHex = 0xFF8B5CF6,
        mediaEmoji = "🎛️",
        reactionsCount = 27,
        myReaction = null,
        topReactions = listOf("🔥", "❤️", "😮"),
        commentsCount = 6,
        sharesCount = 3
      ),
      FacebookPost(
        id = "post_4",
        authorId = "org_arthur",
        authorName = "Arthur Pendelton",
        authorAvatar = "🪵",
        authorBadge = "Master Craftsman",
        isVerifiedAuthor = true,
        groupId = "group_5",
        groupName = "Artisan Woodworking & Joinery Lab",
        groupIcon = "🪵",
        timestampFormatted = "Yesterday at 3:45 PM",
        privacy = PostPrivacy.PUBLIC,
        feelingOrActivity = "🛠️ crafting with hand tools",
        locationTag = "Makerspace Central, Bay 2",
        content = "Completed the hand-cut blind dovetail joints on this black walnut credenza. Zero screws, zero nails—strictly friction and traditional Japanese joinery techniques. Blueprint uploaded to group documents workspace.",
        hashtags = listOf("#Woodworking", "#Joinery", "#HandCrafted", "#Walnut"),
        mediaType = "BLUEPRINT",
        mediaCaption = "Black Walnut Joinery Blueprint (v3.2) - Room Cached",
        mediaColorHex = 0xFFD97706,
        mediaEmoji = "🪵",
        reactionsCount = 64,
        myReaction = ReactionType.LOVE,
        topReactions = listOf("❤️", "🔥", "👍"),
        commentsCount = 19,
        sharesCount = 11
      )
    )

    _comments.value = mapOf(
      "post_1" to listOf(
        FacebookComment(
          id = "c_1",
          postId = "post_1",
          authorName = "Maya Lin",
          authorAvatar = "🎨",
          text = "I'm bringing 5 varieties of organic heirloom peppers to trade! Can't wait for Saturday.",
          timestampFormatted = "10m ago",
          likesCount = 4,
          isLiked = true,
          isVerified = false
        ),
        FacebookComment(
          id = "c_2",
          postId = "post_1",
          authorName = "Alex Rivera (You)",
          authorAvatar = "⚡",
          text = "I've packed the digital soil pH meter and 50L organic compost bags for the group plot.",
          timestampFormatted = "5m ago",
          likesCount = 2,
          isLiked = false,
          isVerified = true
        )
      ),
      "post_2" to listOf(
        FacebookComment(
          id = "c_3",
          postId = "post_2",
          authorName = "Devon Cole",
          authorAvatar = "♟️",
          text = "I've been practicing the Dune Fremen strategy all week. Prepare for battle!",
          timestampFormatted = "1h ago",
          likesCount = 6,
          isLiked = true,
          isVerified = false
        )
      )
    )

    _friends.value = listOf(
      FacebookFriend(
        id = "fr_1",
        name = "Maya Lin",
        avatar = "🎨",
        mutualFriendsCount = 8,
        mutualHobby = "Urban Gardening & Pottery",
        distanceAway = "0.8 km away",
        isFriend = true,
        hasPendingRequest = false,
        isOnline = true,
        isVerified = false
      ),
      FacebookFriend(
        id = "fr_2",
        name = "Elena Rostova",
        avatar = "🌱",
        mutualFriendsCount = 14,
        mutualHobby = "Greenhouse Hydroponics",
        distanceAway = "1.2 km away",
        isFriend = true,
        hasPendingRequest = false,
        isOnline = true,
        isVerified = true
      ),
      FacebookFriend(
        id = "fr_3",
        name = "Marcus Thorne",
        avatar = "🎲",
        mutualFriendsCount = 19,
        mutualHobby = "Tabletop Strategy",
        distanceAway = "1.5 km away",
        isFriend = true,
        hasPendingRequest = false,
        isOnline = true,
        isVerified = true
      ),
      FacebookFriend(
        id = "fr_4",
        name = "Liam Chen",
        avatar = "🛸",
        mutualFriendsCount = 4,
        mutualHobby = "FPV Drone Racing & Avionics",
        distanceAway = "2.1 km away",
        isFriend = false,
        hasPendingRequest = true,
        isOnline = true,
        isVerified = false
      ),
      FacebookFriend(
        id = "fr_5",
        name = "Dr. David Kim",
        avatar = "🔭",
        mutualFriendsCount = 6,
        mutualHobby = "Astrophotography & Optics",
        distanceAway = "3.4 km away",
        isFriend = false,
        hasPendingRequest = false,
        isOnline = false,
        isVerified = true
      ),
      FacebookFriend(
        id = "fr_6",
        name = "Arthur Pendelton",
        avatar = "🪵",
        mutualFriendsCount = 11,
        mutualHobby = "Artisan Woodworking & Joinery",
        distanceAway = "2.8 km away",
        isFriend = false,
        hasPendingRequest = false,
        isOnline = false,
        isVerified = true
      )
    )
  }

  fun reactToPost(postId: String, reaction: ReactionType) {
    val current = _posts.value.toMutableList()
    val index = current.indexOfFirst { it.id == postId }
    if (index != -1) {
      val item = current[index]
      val wasReacted = item.myReaction != null
      val isSame = item.myReaction == reaction

      val updatedReaction = if (isSame) null else reaction
      val countDelta = when {
        isSame -> -1
        !wasReacted -> 1
        else -> 0
      }

      current[index] = item.copy(
        myReaction = updatedReaction,
        reactionsCount = maxOf(0, item.reactionsCount + countDelta),
        topReactions = if (updatedReaction != null && !item.topReactions.contains(updatedReaction.emoji)) {
          listOf(updatedReaction.emoji) + item.topReactions.take(2)
        } else item.topReactions
      )
      _posts.value = current
    }
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
    val newPost = FacebookPost(
      id = "post_${System.currentTimeMillis()}",
      authorId = _userProfile.value.id,
      authorName = _userProfile.value.name,
      authorAvatar = _userProfile.value.avatarEmoji,
      authorBadge = "Verified Creator",
      isVerifiedAuthor = _userProfile.value.isVerifiedOrganizer,
      groupId = groupId,
      groupName = groupName,
      groupIcon = if (groupId != null) "👥" else null,
      timestampFormatted = "Just now",
      privacy = privacy,
      feelingOrActivity = feelingOrActivity,
      locationTag = locationTag ?: _userProfile.value.location,
      content = content,
      hashtags = extractHashtags(content),
      mediaType = mediaType,
      mediaCaption = mediaCaption,
      mediaColorHex = 0xFF0866FF,
      mediaEmoji = "📸",
      reactionsCount = 1,
      myReaction = ReactionType.LIKE,
      topReactions = listOf("👍"),
      commentsCount = 0,
      sharesCount = 0,
      attachedEventId = attachedEventId,
      attachedEventTitle = attachedEventTitle,
      isPinned = false
    )
    _posts.value = listOf(newPost) + _posts.value

    // Notification
    coroutineScope.launch {
      val notif = NotificationEntity(
        id = "notif_${System.currentTimeMillis()}",
        title = "Post Shared to Feed",
        description = "Your hobby update was broadcasted to local community members.",
        timestampFormatted = "Just now",
        type = "CHAT_MESSAGE",
        isRead = false,
        iconEmoji = "📣"
      )
      database.notificationDao().insertNotification(notif)
    }
  }

  fun addComment(postId: String, text: String) {
    val currentMap = _comments.value.toMutableMap()
    val list = currentMap.getOrDefault(postId, emptyList()).toMutableList()
    val newComment = FacebookComment(
      id = "c_${System.currentTimeMillis()}",
      postId = postId,
      authorName = "${_userProfile.value.name} (You)",
      authorAvatar = _userProfile.value.avatarEmoji,
      text = text,
      timestampFormatted = "Just now",
      likesCount = 0,
      isLiked = false,
      isVerified = _userProfile.value.isVerifiedOrganizer
    )
    list.add(newComment)
    currentMap[postId] = list
    _comments.value = currentMap

    // Increment post comment count
    val postList = _posts.value.toMutableList()
    val pIndex = postList.indexOfFirst { it.id == postId }
    if (pIndex != -1) {
      postList[pIndex] = postList[pIndex].copy(commentsCount = postList[pIndex].commentsCount + 1)
      _posts.value = postList
    }
  }

  fun toggleLikeComment(postId: String, commentId: String) {
    val currentMap = _comments.value.toMutableMap()
    val list = currentMap.getOrDefault(postId, emptyList()).toMutableList()
    val index = list.indexOfFirst { it.id == commentId }
    if (index != -1) {
      val c = list[index]
      val next = !c.isLiked
      list[index] = c.copy(
        isLiked = next,
        likesCount = if (next) c.likesCount + 1 else maxOf(0, c.likesCount - 1)
      )
      currentMap[postId] = list
      _comments.value = currentMap
    }
  }

  fun sharePost(postId: String) {
    val postList = _posts.value.toMutableList()
    val pIndex = postList.indexOfFirst { it.id == postId }
    if (pIndex != -1) {
      postList[pIndex] = postList[pIndex].copy(sharesCount = postList[pIndex].sharesCount + 1)
      _posts.value = postList
    }
  }

  fun toggleFriend(friendId: String) {
    val list = _friends.value.toMutableList()
    val index = list.indexOfFirst { it.id == friendId }
    if (index != -1) {
      val item = list[index]
      val nextFriend = !item.isFriend
      list[index] = item.copy(isFriend = nextFriend, hasPendingRequest = false)
      _friends.value = list
    }
  }

  fun acceptFriendRequest(friendId: String) {
    val list = _friends.value.toMutableList()
    val index = list.indexOfFirst { it.id == friendId }
    if (index != -1) {
      list[index] = list[index].copy(isFriend = true, hasPendingRequest = false)
      _friends.value = list
    }
  }

  fun markStorySeen(storyId: String) {
    val list = _stories.value.toMutableList()
    val index = list.indexOfFirst { it.id == storyId }
    if (index != -1) {
      list[index] = list[index].copy(isSeen = true)
      _stories.value = list
    }
  }

  private fun extractHashtags(text: String): List<String> {
    return text.split("\\s+".toRegex())
      .filter { it.startsWith("#") && it.length > 1 }
  }


  // --- Seed Data ---
  private suspend fun seedInitialDataIfNeeded() = withContext(Dispatchers.IO) {
    val groupCount = database.groupDao().getGroupById("group_1")
    if (groupCount == null) {
      val initialGroups = listOf(
        GroupEntity(
          id = "group_1",
          name = "Metropolis Board Game Guild",
          category = "Board Games",
          description = "Competitive and casual tabletop enthusiasts. Heavy Eurogames, social deduction, and prototype playtesting.",
          isPrivate = false,
          membersCount = 142,
          organizerId = "org_marcus",
          organizerName = "Marcus Thorne",
          organizerReputation = 99,
          isVerifiedOrganizer = true,
          locationName = "Downtown Arts District (The Boardroom)",
          latitude = 37.7749,
          longitude = -122.4194,
          iconEmoji = "🎲",
          coverColorHex = 0xFF4F46E5,
          tagsJoined = "Tabletop,Eurogames,Catan,Dune,Prototype Testing",
          rulesJoined = "Be respectful of game components,RSVP 24h prior,Host supplies snacks on rotation",
          isMember = true,
          membershipRole = "MEMBER"
        ),
        GroupEntity(
          id = "group_2",
          name = "GreenThumb Urban Gardeners",
          category = "Urban Gardening",
          description = "Micro-farming, rooftop hydroponics, and heirloom seed sharing across neighborhood plots.",
          isPrivate = false,
          membersCount = 89,
          organizerId = "org_elena",
          organizerName = "Elena Rostova",
          organizerReputation = 98,
          isVerifiedOrganizer = true,
          locationName = "Riverside Community Allotment",
          latitude = 37.7833,
          longitude = -122.4167,
          iconEmoji = "🌱",
          coverColorHex = 0xFF10B981,
          tagsJoined = "Permaculture,Composting,Hydroponics,Heirloom Seeds,Balcony Farming",
          rulesJoined = "Clean tools after use,Share harvest surplus,Organic practices only",
          isMember = true,
          membershipRole = "CO_HOST"
        ),
        GroupEntity(
          id = "group_3",
          name = "FPV Drone Racers & Aero Hackers",
          category = "Drone & Tech",
          description = "High-speed brushless drone racing, custom telemetry firmware tuning, and obstacle freestyle sessions.",
          isPrivate = true,
          membersCount = 64,
          organizerId = "org_liam",
          organizerName = "Liam Chen",
          organizerReputation = 96,
          isVerifiedOrganizer = true,
          locationName = "North Sky Airfield (Zone 4)",
          latitude = 37.7650,
          longitude = -122.4300,
          iconEmoji = "🛸",
          coverColorHex = 0xFF0284C7,
          tagsJoined = "FPV Racing,Betaflight,Custom Builds,Freestyle,Soldering",
          rulesJoined = "Register VTX frequency on check-in,Spotters mandatory,Fail-safe tests required",
          isMember = false,
          membershipRole = "NON_MEMBER"
        ),
        GroupEntity(
          id = "group_4",
          name = "Analogue Synth & Modular Audio Jam",
          category = "Synth & Audio",
          description = "Eurorack patching, hardware sound design, tape loops, and collaborative ambient electronic sets.",
          isPrivate = false,
          membersCount = 53,
          organizerId = "org_nina",
          organizerName = "Nina Patel",
          organizerReputation = 97,
          isVerifiedOrganizer = true,
          locationName = "Underground Sound Lab, Unit 3B",
          latitude = 37.7700,
          longitude = -122.4100,
          iconEmoji = "🎛️",
          coverColorHex = 0xFF8B5CF6,
          tagsJoined = "Eurorack,Sound Design,Moog,Tape Loops,Ambient Jam",
          rulesJoined = "BYO 3.5mm cables,Master limiter strictly active,Recordings shared under CC-BY",
          isMember = true,
          membershipRole = "MEMBER"
        ),
        GroupEntity(
          id = "group_5",
          name = "Artisan Woodworking & Joinery Lab",
          category = "Woodworking",
          description = "Hand-tool craftsmanship, traditional Japanese joinery, lathe turning, and custom furniture fabrication.",
          isPrivate = true,
          membersCount = 78,
          organizerId = "org_arthur",
          organizerName = "Arthur Pendelton",
          organizerReputation = 99,
          isVerifiedOrganizer = true,
          locationName = "Makerspace Central, Bay 2",
          latitude = 37.7600,
          longitude = -122.4220,
          iconEmoji = "🪵",
          coverColorHex = 0xFFD97706,
          tagsJoined = "Hand Tools,Japanese Joinery,Woodturning,Finishing,Dovetails",
          rulesJoined = "Eye & ear protection mandatory,Clean sawdust extraction,Tag personal timber",
          isMember = false,
          membershipRole = "NON_MEMBER"
        ),
        GroupEntity(
          id = "group_6",
          name = "Midnight Stargazers & Astrophotography",
          category = "Astronomy",
          description = "Telescope deep-sky observation, planetary imaging, tracker calibration, and dark sky expeditions.",
          isPrivate = false,
          membersCount = 41,
          organizerId = "org_samira",
          organizerName = "Samira Khan",
          organizerReputation = 98,
          isVerifiedOrganizer = true,
          locationName = "Twin Peaks Observatory Point",
          latitude = 37.7544,
          longitude = -122.4477,
          iconEmoji = "🔭",
          coverColorHex = 0xFF312E81,
          tagsJoined = "Astrophotography,Telescopes,Star Maps,Deep Sky,Dark Sky Trips",
          rulesJoined = "Red light torches only,No white lights near eyepieces,Dress warmly",
          isMember = false,
          membershipRole = "NON_MEMBER"
        )
      )
      database.groupDao().insertAll(initialGroups)

      val initialEvents = listOf(
        EventEntity(
          id = "event_1",
          groupId = "group_1",
          groupName = "Metropolis Board Game Guild",
          title = "Heavy Euro Night: Dune Imperium & Brass Birmingham",
          category = "Board Games",
          description = "Deep strategy tabletop session with tournament timers, custom 3D printed components, and hot tea.",
          dateFormatted = "Tomorrow",
          timeFormatted = "6:30 PM - 10:30 PM",
          locationName = "The Boardroom Cafe, Table 4-6",
          latitude = 37.7749,
          longitude = -122.4194,
          organizerName = "Marcus Thorne",
          organizerReputation = 99,
          isVerifiedOrganizer = true,
          maxAttendees = 12,
          currentRsvpCount = 9,
          userRsvp = "GOING",
          meetingType = "IN_PERSON",
          isLiveNow = false,
          equipmentRaw = "Dune Imperium:Uprising (Marcus Thorne):true;;Brass Birmingham Deluxe (Alex Rivera):true;;Game Timer App (Shared):true;;Player Mats (Needs Volunteer):false",
          coverIcon = "🎲"
        ),
        EventEntity(
          id = "event_2",
          groupId = "group_2",
          groupName = "GreenThumb Urban Gardeners",
          title = "Heirloom Seed Swap & Soil pH Testing Workshop",
          category = "Urban Gardening",
          description = "Bring saved seeds from last season to trade! We will also test soil mineral compositions and calibrate irrigation drippers.",
          dateFormatted = "Saturday, 10:00 AM",
          timeFormatted = "10:00 AM - 1:00 PM",
          locationName = "Riverside Community Pavilion",
          latitude = 37.7833,
          longitude = -122.4167,
          organizerName = "Elena Rostova",
          organizerReputation = 98,
          isVerifiedOrganizer = true,
          maxAttendees = 30,
          currentRsvpCount = 22,
          userRsvp = "GOING",
          meetingType = "HYBRID",
          isLiveNow = true,
          equipmentRaw = "Digital Soil pH Pen (Elena Rostova):true;;Seed Packet Envelopes (Elena):true;;Potting Soil Bags 50L (Alex Rivera):true;;Magnifier Loupes (Needs Volunteer):false",
          coverIcon = "🌱"
        ),
        EventEntity(
          id = "event_3",
          groupId = "group_3",
          groupName = "FPV Drone Racers & Aero Hackers",
          title = "Underground Neon Gate Qualifying & Time-Trials",
          category = "Drone & Tech",
          description = "High-speed 6S 5-inch drone sprints through programmable LED gates with live telemetry scoreboard.",
          dateFormatted = "Friday, 8:00 PM",
          timeFormatted = "8:00 PM - 11:30 PM",
          locationName = "North Sky Airfield Warehouse",
          latitude = 37.7650,
          longitude = -122.4300,
          organizerName = "Liam Chen",
          organizerReputation = 96,
          isVerifiedOrganizer = true,
          maxAttendees = 20,
          currentRsvpCount = 18,
          userRsvp = "NONE",
          meetingType = "IN_PERSON",
          isLiveNow = false,
          equipmentRaw = "LapRF Timing System (Liam):true;;LED Racing Gates (Liam):true;;LiPo Fireproof Charging Station (Alex):false",
          coverIcon = "🛸"
        ),
        EventEntity(
          id = "event_4",
          groupId = "group_4",
          groupName = "Analogue Synth & Modular Audio Jam",
          title = "Collaborative Ambient Drone & Tape Echo Session",
          category = "Synth & Audio",
          description = "Live hybrid patch session: bring your gear or join the low-latency audio stream stage from home.",
          dateFormatted = "Sunday, 4:00 PM",
          timeFormatted = "4:00 PM - 8:00 PM",
          locationName = "Underground Sound Lab / Virtual Stage",
          latitude = 37.7700,
          longitude = -122.4100,
          organizerName = "Nina Patel",
          organizerReputation = 97,
          isVerifiedOrganizer = true,
          maxAttendees = 15,
          currentRsvpCount = 11,
          userRsvp = "MAYBE",
          meetingType = "VIRTUAL_AUDIO_STAGE",
          isLiveNow = false,
          equipmentRaw = "16-Channel Audio Interface (Nina):true;;Reel-to-Reel Tape Echo (Alex):true;;Patch Cables 50-pack (Nina):true",
          coverIcon = "🎛️"
        ),
        EventEntity(
          id = "event_5",
          groupId = "group_6",
          groupName = "Midnight Stargazers & Astrophotography",
          title = "Perseid Meteor Shower Watch & Long-Exposure Shoot",
          category = "Astronomy",
          description = "High-altitude clear sky shoot. We will track the meteor radiant and stack deep-sky nebulae exposures.",
          dateFormatted = "Next Wednesday, 10:30 PM",
          timeFormatted = "10:30 PM - 3:00 AM",
          locationName = "Twin Peaks Observatory Point",
          latitude = 37.7544,
          longitude = -122.4477,
          organizerName = "Samira Khan",
          organizerReputation = 98,
          isVerifiedOrganizer = true,
          maxAttendees = 25,
          currentRsvpCount = 19,
          userRsvp = "NONE",
          meetingType = "IN_PERSON",
          isLiveNow = false,
          equipmentRaw = "8-inch Dobsonian Telescope (Samira):true;;Star Tracker Mount (Devon):true;;Thermal Flasks (Everyone):false",
          coverIcon = "🔭"
        )
      )
      database.eventDao().insertAll(initialEvents)

      val initialMessages = listOf(
        MessageEntity(
          id = "msg_1",
          conversationId = "group_1",
          senderId = "org_marcus",
          senderName = "Marcus Thorne",
          senderAvatar = "🎲",
          text = "Welcome everyone! I've booked Table 4 for tomorrow's Dune Imperium match. Please check the equipment list to verify who is bringing what.",
          timestampFormatted = "10:14 AM",
          isEncrypted = true,
          mediaType = "NONE",
          mediaCaption = "",
          isFromMe = false,
          syncStatus = "SYNCED"
        ),
        MessageEntity(
          id = "msg_2",
          conversationId = "group_1",
          senderId = "user_me",
          senderName = "Alex Rivera",
          senderAvatar = "⚡",
          text = "I'm bringing the deluxe Brass Birmingham edition with metal coins as requested!",
          timestampFormatted = "10:20 AM",
          isEncrypted = true,
          mediaType = "IMAGE",
          mediaCaption = "brass_deluxe_box_preview.jpg (Encrypted SHA-256)",
          isFromMe = true,
          syncStatus = "SYNCED"
        ),
        MessageEntity(
          id = "msg_3",
          conversationId = "group_1",
          senderId = "user_maya",
          senderName = "Maya Lin",
          senderAvatar = "🎨",
          text = "Awesome Alex! I will bring score pads and 3D printed resource trays for the table.",
          timestampFormatted = "10:28 AM",
          isEncrypted = true,
          mediaType = "NONE",
          mediaCaption = "",
          isFromMe = false,
          syncStatus = "SYNCED"
        ),
        MessageEntity(
          id = "msg_4",
          conversationId = "group_2",
          senderId = "org_elena",
          senderName = "Elena Rostova",
          senderAvatar = "🌱",
          text = "Good morning gardeners! The greenhouse hydroponic pump is operating at optimal pH 6.2. The seedling distribution begins at 10 AM.",
          timestampFormatted = "09:05 AM",
          isEncrypted = true,
          mediaType = "DOCUMENT",
          mediaCaption = "Hydroponic_Nutrient_Formula_v2.pdf",
          isFromMe = false,
          syncStatus = "SYNCED"
        ),
        MessageEntity(
          id = "msg_5",
          conversationId = "dm_elena",
          senderId = "org_elena",
          senderName = "Elena Rostova",
          senderAvatar = "🌱",
          text = "Hi Alex, thanks for co-hosting Saturday's workshop. Could you verify the audio equipment setup for the hybrid broadcast?",
          timestampFormatted = "Yesterday, 4:45 PM",
          isEncrypted = true,
          mediaType = "NONE",
          mediaCaption = "",
          isFromMe = false,
          syncStatus = "SYNCED"
        ),
        MessageEntity(
          id = "msg_6",
          conversationId = "dm_elena",
          senderId = "user_me",
          senderName = "Alex Rivera",
          senderAvatar = "⚡",
          text = "All set Elena! Wireless lavalier mics are charged and the offline recorder is configured.",
          timestampFormatted = "Yesterday, 5:10 PM",
          isEncrypted = true,
          mediaType = "NONE",
          mediaCaption = "",
          isFromMe = true,
          syncStatus = "SYNCED"
        )
      )
      database.messageDao().insertAll(initialMessages)

      val initialDocs = listOf(
        DocumentEntity(
          id = "doc_1",
          groupId = "group_1",
          groupName = "Metropolis Board Game Guild",
          title = "2026 Tabletop League Tournament Rules & Scoring Matrix",
          content = """
# Metropolis Board Game Guild League 2026

## 1. Season Overview
- **Season Length**: 8 Rounds across 4 months.
- **Scoring System**:
  - 1st Place: 4 Points
  - 2nd Place: 2 Points
  - 3rd Place: 1 Point
  - Tie-breaker: VP Margin percentage.

## 2. Certified Game Pool
- *Dune: Imperium - Uprising*
- *Brass: Birmingham (Deluxe)*
- *Terraforming Mars (Prelude 2)*
- *Ark Nova + Marine Worlds*

## 3. Organizer Fair Play Standards
All games must be played with sleeved cards and verified rulebook editions.
Organizers maintain verified reputation scores (current min threshold: 95%).
          """.trimIndent(),
          lastModified = "Just now",
          activeEditorsJoined = "Marcus Thorne,Alex Rivera,Maya Lin",
          version = 4
        ),
        DocumentEntity(
          id = "doc_2",
          groupId = "group_2",
          groupName = "GreenThumb Urban Gardeners",
          title = "Community Greenhouse Planting Schedule & Micro-Climate Chart",
          content = """
# Urban Garden Micro-Climate & Crop Rotation Blueprint

## Plot 1: Raised Beds (Full Sun)
- **March**: Heirloom Tomatoes (Cherokee Purple, Brandywine)
- **Companion Plants**: Sweet Basil, French Marigolds (Nematode control)
- **Target Soil pH**: 6.2 - 6.8

## Plot 2: Shaded Trellis (North Wall)
- **Crops**: Sugar Snap Peas, Romanesco Cauliflower, Butterhead Lettuce
- **Drip Schedule**: 15 minutes twice daily at 07:00 and 19:00.

## Hydroponic Tower Specifications
- EC Level: 1.8 - 2.2 mS/cm
- Water Tank Temp: 19°C - 21°C constant.
          """.trimIndent(),
          lastModified = "12 mins ago",
          activeEditorsJoined = "Elena Rostova,Alex Rivera",
          version = 7
        ),
        DocumentEntity(
          id = "doc_3",
          groupId = "group_4",
          groupName = "Analogue Synth & Modular Audio Jam",
          title = "Modular Clock Sync & Voltage Standard Specification",
          content = """
# Modular Jam Patching Guidelines & Master Clock

## 1. Clock Distribution
- Master Clock generated via Pamela's PRO Workout (120 BPM base).
- 1V/Octave standard calibration before live performance.

## 2. Mixing & Headroom
- Peak limit per sub-mixer channel: -6dB.
- Ambient drone beds to be panned stereo 40% - 60%.
          """.trimIndent(),
          lastModified = "2 hours ago",
          activeEditorsJoined = "Nina Patel,Devon Cole",
          version = 2
        )
      )
      database.documentDao().insertAll(initialDocs)

      val initialMilestones = listOf(
        MilestoneItemEntityConverter.toEntity(
          MilestoneItem(
            id = "mile_1",
            groupId = "group_2",
            title = "Install Automated Drip Irrigation Controller",
            dueDate = "Oct 15",
            assignedMember = "Alex Rivera",
            isCompleted = true,
            progressPercent = 100
          )
        ),
        MilestoneItemEntityConverter.toEntity(
          MilestoneItem(
            id = "mile_2",
            groupId = "group_2",
            title = "Construct Winterized Seedling Hotbed",
            dueDate = "Nov 02",
            assignedMember = "Elena Rostova",
            isCompleted = false,
            progressPercent = 75
          )
        ),
        MilestoneItemEntityConverter.toEntity(
          MilestoneItem(
            id = "mile_3",
            groupId = "group_1",
            title = "Fabricate Hand-Crafted Walnut League Trophy",
            dueDate = "Nov 20",
            assignedMember = "Arthur Pendelton",
            isCompleted = false,
            progressPercent = 40
          )
        ),
        MilestoneItemEntityConverter.toEntity(
          MilestoneItem(
            id = "mile_4",
            groupId = "group_4",
            title = "Multi-Track Soundboard Patchbay Calibration",
            dueDate = "Oct 28",
            assignedMember = "Nina Patel",
            isCompleted = true,
            progressPercent = 100
          )
        )
      )
      database.milestoneDao().insertAll(initialMilestones)

      val initialNotifications = listOf(
        NotificationEntity(
          id = "notif_1",
          title = "Event Reminder: Heavy Euro Night",
          description = "Metropolis Board Game Guild meets tomorrow at 6:30 PM at The Boardroom Cafe.",
          timestampFormatted = "10 mins ago",
          type = "EVENT_UPDATE",
          isRead = false,
          iconEmoji = "🎲"
        ),
        NotificationEntity(
          id = "notif_2",
          title = "Verified Organizer Endorsement",
          description = "Elena Rostova received 14 new peer trust votes. Reliability rating: 98% (Exceptional Host).",
          timestampFormatted = "1 hour ago",
          type = "REPUTATION_BADGE",
          isRead = false,
          iconEmoji = "⭐"
        ),
        NotificationEntity(
          id = "notif_3",
          title = "Encrypted Group Document Updated",
          description = "Marcus Thorne made updates to '2026 Tabletop League Tournament Rules'.",
          timestampFormatted = "3 hours ago",
          type = "SYNC_STATUS",
          isRead = true,
          iconEmoji = "📝"
        ),
        NotificationEntity(
          id = "notif_4",
          title = "Offline Cache Synchronized",
          description = "All local messages and event changes were merged securely with cloud storage.",
          timestampFormatted = "Yesterday",
          type = "SYNC_STATUS",
          isRead = true,
          iconEmoji = "☁️"
        )
      )
      database.notificationDao().insertAll(initialNotifications)
    }
  }

  // --- Groups ---
  val allGroups: Flow<List<HobbyGroup>> = database.groupDao().getAllGroups().map { list ->
    list.map { it.toDomain() }
  }

  suspend fun toggleGroupMembership(groupId: String, currentStatus: Boolean) = withContext(Dispatchers.IO) {
    val newStatus = !currentStatus
    val delta = if (newStatus) 1 else -1
    database.groupDao().updateMembership(groupId, newStatus, delta)
    // Add notification
    val notif = NotificationEntity(
      id = "notif_${System.currentTimeMillis()}",
      title = if (newStatus) "Joined Interest Group" else "Left Group",
      description = "Your membership was updated and synced offline.",
      timestampFormatted = "Just now",
      type = "GROUP_INVITE",
      isRead = false,
      iconEmoji = if (newStatus) "🎉" else "ℹ️"
    )
    database.notificationDao().insertNotification(notif)
  }

  suspend fun createGroup(
    name: String,
    category: String,
    description: String,
    isPrivate: Boolean,
    locationName: String,
    latitude: Double,
    longitude: Double,
    iconEmoji: String,
    tags: List<String>,
    rules: List<String>
  ) = withContext(Dispatchers.IO) {
    val newId = "group_${System.currentTimeMillis()}"
    val newGroup = GroupEntity(
      id = newId,
      name = name,
      category = category,
      description = description,
      isPrivate = isPrivate,
      membersCount = 1,
      organizerId = _userProfile.value.id,
      organizerName = _userProfile.value.name,
      organizerReputation = _userProfile.value.reputationScore,
      isVerifiedOrganizer = _userProfile.value.isVerifiedOrganizer,
      locationName = locationName,
      latitude = latitude,
      longitude = longitude,
      iconEmoji = iconEmoji,
      coverColorHex = 0xFF4F46E5,
      tagsJoined = tags.joinToString(","),
      rulesJoined = rules.joinToString(","),
      isMember = true,
      membershipRole = "ORGANIZER"
    )
    database.groupDao().insertGroup(newGroup)
  }

  // --- Events ---
  val allEvents: Flow<List<HobbyEvent>> = database.eventDao().getAllEvents().map { list ->
    list.map { it.toDomain() }
  }

  suspend fun updateEventRsvp(eventId: String, newStatus: EventRsvpStatus) = withContext(Dispatchers.IO) {
    val event = database.eventDao().getEventById(eventId) ?: return@withContext
    val currentRsvp = EventRsvpStatus.valueOf(event.userRsvp)
    var count = event.currentRsvpCount
    if (currentRsvp != EventRsvpStatus.GOING && newStatus == EventRsvpStatus.GOING) {
      count += 1
    } else if (currentRsvp == EventRsvpStatus.GOING && newStatus != EventRsvpStatus.GOING) {
      count = maxOf(0, count - 1)
    }
    database.eventDao().updateRsvp(eventId, newStatus.name, count)

    // Notification
    val notif = NotificationEntity(
      id = "notif_${System.currentTimeMillis()}",
      title = "RSVP Updated: ${event.title}",
      description = "Your RSVP status is now ${newStatus.name.lowercase().replaceFirstChar { it.uppercase() }}.",
      timestampFormatted = "Just now",
      type = "EVENT_UPDATE",
      isRead = false,
      iconEmoji = "📅"
    )
    database.notificationDao().insertNotification(notif)
  }

  suspend fun toggleEquipmentItem(eventId: String, itemId: String) = withContext(Dispatchers.IO) {
    val event = database.eventDao().getEventById(eventId) ?: return@withContext
    val items = parseEquipmentList(event.equipmentRaw).toMutableList()
    val idx = items.indexOfFirst { it.id == itemId }
    if (idx != -1) {
      val item = items[idx]
      items[idx] = item.copy(
        isChecked = !item.isChecked,
        assignedTo = if (!item.isChecked) _userProfile.value.name else ""
      )
      val updatedRaw = serializeEquipmentList(items)
      database.eventDao().updateEquipment(eventId, updatedRaw)
    }
  }

  suspend fun createEvent(
    groupId: String,
    title: String,
    category: String,
    description: String,
    dateFormatted: String,
    timeFormatted: String,
    locationName: String,
    latitude: Double,
    longitude: Double,
    maxAttendees: Int,
    meetingType: EventMeetingType,
    equipmentList: List<EquipmentItem>
  ) = withContext(Dispatchers.IO) {
    val group = database.groupDao().getGroupById(groupId)
    val newId = "event_${System.currentTimeMillis()}"
    val newEvent = EventEntity(
      id = newId,
      groupId = groupId,
      groupName = group?.name ?: "Interest Group",
      title = title,
      category = category,
      description = description,
      dateFormatted = dateFormatted,
      timeFormatted = timeFormatted,
      locationName = locationName,
      latitude = latitude,
      longitude = longitude,
      organizerName = _userProfile.value.name,
      organizerReputation = _userProfile.value.reputationScore,
      isVerifiedOrganizer = _userProfile.value.isVerifiedOrganizer,
      maxAttendees = maxAttendees,
      currentRsvpCount = 1,
      userRsvp = "GOING",
      meetingType = meetingType.name,
      isLiveNow = false,
      equipmentRaw = serializeEquipmentList(equipmentList),
      coverIcon = group?.iconEmoji ?: "📅"
    )
    database.eventDao().insertEvent(newEvent)
  }

  // --- Messages & Chat ---
  fun getMessagesForConversation(convId: String): Flow<List<ChatMessage>> {
    return database.messageDao().getMessagesForConversation(convId).map { list ->
      list.map { it.toDomain() }
    }
  }

  val allMessages: Flow<List<ChatMessage>> = database.messageDao().getAllMessages().map { list ->
    list.map { it.toDomain() }
  }

  suspend fun sendMessage(
    conversationId: String,
    text: String,
    mediaType: ChatMediaType = ChatMediaType.NONE,
    mediaCaption: String = ""
  ) = withContext(Dispatchers.IO) {
    val newId = "msg_${System.currentTimeMillis()}"
    val msg = MessageEntity(
      id = newId,
      conversationId = conversationId,
      senderId = _userProfile.value.id,
      senderName = _userProfile.value.name,
      senderAvatar = _userProfile.value.avatarEmoji,
      text = text,
      timestampFormatted = "Just now",
      isEncrypted = true,
      mediaType = mediaType.name,
      mediaCaption = mediaCaption,
      isFromMe = true,
      syncStatus = if (_userProfile.value.offlineModeActive) "PENDING_OFFLINE" else "SYNCED"
    )
    database.messageDao().insertMessage(msg)
  }

  // --- Collaborative Documents ---
  val allDocuments: Flow<List<CollabDocument>> = database.documentDao().getAllDocuments().map { list ->
    list.map { it.toDomain() }
  }

  suspend fun updateDocumentContent(docId: String, content: String) = withContext(Dispatchers.IO) {
    database.documentDao().updateDocumentContent(docId, content, "Just now")
  }

  suspend fun createDocument(groupId: String, groupName: String, title: String, content: String) = withContext(Dispatchers.IO) {
    val newDoc = DocumentEntity(
      id = "doc_${System.currentTimeMillis()}",
      groupId = groupId,
      groupName = groupName,
      title = title,
      content = content,
      lastModified = "Just now",
      activeEditorsJoined = "${_userProfile.value.name} (You)",
      version = 1
    )
    database.documentDao().insertDocument(newDoc)
  }

  // --- Milestones ---
  val allMilestones: Flow<List<MilestoneItem>> = database.milestoneDao().getAllMilestones().map { list ->
    list.map { it.toDomain() }
  }

  suspend fun toggleMilestone(id: String, isCompleted: Boolean) = withContext(Dispatchers.IO) {
    val newStatus = !isCompleted
    val newProgress = if (newStatus) 100 else 50
    database.milestoneDao().updateMilestoneStatus(id, newStatus, newProgress)
  }

  suspend fun createMilestone(groupId: String, title: String, dueDate: String, assignedMember: String) = withContext(Dispatchers.IO) {
    val item = MilestoneEntity(
      id = "mile_${System.currentTimeMillis()}",
      groupId = groupId,
      title = title,
      dueDate = dueDate,
      assignedMember = assignedMember,
      isCompleted = false,
      progressPercent = 0
    )
    database.milestoneDao().insertMilestone(item)
  }

  // --- Notifications ---
  val allNotifications: Flow<List<AppNotification>> = database.notificationDao().getAllNotifications().map { list ->
    list.map { it.toDomain() }
  }

  suspend fun markNotificationRead(id: String) = withContext(Dispatchers.IO) {
    database.notificationDao().markAsRead(id)
  }

  suspend fun markAllNotificationsRead() = withContext(Dispatchers.IO) {
    database.notificationDao().markAllAsRead()
  }

  // --- Profile & Offline Sync Controls ---
  fun updateDarkMode(mode: String) {
    _userProfile.value = _userProfile.value.copy(darkModePreference = mode)
  }

  fun toggleOfflineMode() {
    val current = _userProfile.value.offlineModeActive
    val next = !current
    _userProfile.value = _userProfile.value.copy(offlineModeActive = next)
    _syncState.value = if (next) SyncState.OFFLINE_CACHED else SyncState.ONLINE_SYNCED
  }

  suspend fun triggerManualSync() = withContext(Dispatchers.IO) {
    _syncState.value = SyncState.SYNCING_IN_PROGRESS
    kotlinx.coroutines.delay(1200) // Simulated fast cloud synchronization
    _syncState.value = SyncState.ONLINE_SYNCED
    val notif = NotificationEntity(
      id = "notif_${System.currentTimeMillis()}",
      title = "Cloud Synchronization Completed",
      description = "Encrypted local state synced with cross-platform storage without conflicts.",
      timestampFormatted = "Just now",
      type = "SYNC_STATUS",
      isRead = false,
      iconEmoji = "✨"
    )
    database.notificationDao().insertNotification(notif)
  }

  fun endorseOrganizer(scoreDelta: Int) {
    val current = _userProfile.value
    val newScore = minOf(100, current.reputationScore + scoreDelta)
    _userProfile.value = current.copy(reputationScore = newScore)
  }

  // --- Mappers ---
  private fun GroupEntity.toDomain(): HobbyGroup {
    return HobbyGroup(
      id = id,
      name = name,
      category = category,
      description = description,
      isPrivate = isPrivate,
      membersCount = membersCount,
      organizerId = organizerId,
      organizerName = organizerName,
      organizerReputation = organizerReputation,
      isVerifiedOrganizer = isVerifiedOrganizer,
      locationName = locationName,
      latitude = latitude,
      longitude = longitude,
      iconEmoji = iconEmoji,
      coverColorHex = coverColorHex,
      tags = if (tagsJoined.isEmpty()) emptyList() else tagsJoined.split(","),
      rules = if (rulesJoined.isEmpty()) emptyList() else rulesJoined.split(","),
      isMember = isMember,
      membershipRole = membershipRole
    )
  }

  private fun EventEntity.toDomain(): HobbyEvent {
    return HobbyEvent(
      id = id,
      groupId = groupId,
      groupName = groupName,
      title = title,
      category = category,
      description = description,
      dateFormatted = dateFormatted,
      timeFormatted = timeFormatted,
      locationName = locationName,
      latitude = latitude,
      longitude = longitude,
      organizerName = organizerName,
      organizerReputation = organizerReputation,
      isVerifiedOrganizer = isVerifiedOrganizer,
      maxAttendees = maxAttendees,
      currentRsvpCount = currentRsvpCount,
      userRsvp = try { EventRsvpStatus.valueOf(userRsvp) } catch (e: Exception) { EventRsvpStatus.NONE },
      meetingType = try { EventMeetingType.valueOf(meetingType) } catch (e: Exception) { EventMeetingType.IN_PERSON },
      isLiveNow = isLiveNow,
      equipmentList = parseEquipmentList(equipmentRaw),
      coverIcon = coverIcon
    )
  }

  private fun MessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
      id = id,
      conversationId = conversationId,
      senderId = senderId,
      senderName = senderName,
      senderAvatar = senderAvatar,
      text = text,
      timestampFormatted = timestampFormatted,
      isEncrypted = isEncrypted,
      mediaType = try { ChatMediaType.valueOf(mediaType) } catch (e: Exception) { ChatMediaType.NONE },
      mediaCaption = mediaCaption,
      isFromMe = isFromMe,
      syncStatus = syncStatus
    )
  }

  private fun DocumentEntity.toDomain(): CollabDocument {
    return CollabDocument(
      id = id,
      groupId = groupId,
      groupName = groupName,
      title = title,
      content = content,
      lastModified = lastModified,
      activeEditors = if (activeEditorsJoined.isEmpty()) emptyList() else activeEditorsJoined.split(","),
      version = version
    )
  }

  private fun MilestoneEntity.toDomain(): MilestoneItem {
    return MilestoneItem(
      id = id,
      groupId = groupId,
      title = title,
      dueDate = dueDate,
      assignedMember = assignedMember,
      isCompleted = isCompleted,
      progressPercent = progressPercent
    )
  }

  private fun NotificationEntity.toDomain(): AppNotification {
    return AppNotification(
      id = id,
      title = title,
      description = description,
      timestampFormatted = timestampFormatted,
      type = type,
      isRead = isRead,
      iconEmoji = iconEmoji
    )
  }

  companion object {
    fun parseEquipmentList(raw: String): List<EquipmentItem> {
      if (raw.isEmpty()) return emptyList()
      return raw.split(";;").mapIndexedNotNull { index, itemStr ->
        val parts = itemStr.split(":")
        if (parts.isNotEmpty()) {
          val name = parts.getOrNull(0) ?: ""
          val assignedTo = parts.getOrNull(1) ?: ""
          val isChecked = parts.getOrNull(2)?.toBoolean() ?: false
          EquipmentItem(
            id = "eq_$index",
            name = name,
            assignedTo = assignedTo,
            isChecked = isChecked
          )
        } else null
      }
    }

    fun serializeEquipmentList(items: List<EquipmentItem>): String {
      return items.joinToString(";;") { "${it.name}:${it.assignedTo}:${it.isChecked}" }
    }
  }
}

object MilestoneItemEntityConverter {
  fun toEntity(item: MilestoneItem): MilestoneEntity {
    return MilestoneEntity(
      id = item.id,
      groupId = item.groupId,
      title = item.title,
      dueDate = item.dueDate,
      assignedMember = item.assignedMember,
      isCompleted = item.isCompleted,
      progressPercent = item.progressPercent
    )
  }
}
