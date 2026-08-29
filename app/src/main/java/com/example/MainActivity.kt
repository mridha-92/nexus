package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.model.EventRsvpStatus
import com.example.data.model.FacebookFriend
import com.example.data.model.FacebookPost
import com.example.data.model.PostPrivacy
import com.example.data.model.ReactionType
import com.example.ui.components.CreatePostDialog
import com.example.ui.components.FacebookCommentsSheet
import com.example.ui.components.FacebookTopBar
import com.example.ui.components.StoryViewerDialog
import com.example.ui.components.SyncStatusBanner
import com.example.ui.screens.chat.ChatConversationScreen
import com.example.ui.screens.chat.ChatListScreen
import com.example.ui.screens.collab.CollabWorkspaceScreen
import com.example.ui.screens.collab.DocumentEditorScreen
import com.example.ui.screens.collab.VideoLoungeScreen
import com.example.ui.screens.discovery.MapDiscoveryScreen
import com.example.ui.screens.events.CreateEventDialog
import com.example.ui.screens.events.EventDetailScreen
import com.example.ui.screens.events.EventsScreen
import com.example.ui.screens.feed.FacebookFeedScreen
import com.example.ui.screens.friends.FacebookFriendsScreen
import com.example.ui.screens.groups.GroupDetailScreen
import com.example.ui.screens.groups.GroupsScreen
import com.example.ui.screens.menu.FacebookMenuScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.onboarding.OnboardingTutorialDialog
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.theme.FacebookBlue
import com.example.ui.theme.HobbyCircleTheme
import com.example.ui.viewmodel.HobbyViewModel

enum class FacebookTab(
  val route: String,
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val badgeCount: Int = 0
) {
  FEED("feed", "Home", Icons.Filled.Home, Icons.Outlined.Home),
  FRIENDS("friends", "Friends", Icons.Filled.People, Icons.Outlined.People),
  GROUPS("groups", "Groups", Icons.Filled.Groups, Icons.Outlined.Groups),
  EVENTS("events", "Events", Icons.Filled.Event, Icons.Outlined.Event),
  NOTIFICATIONS("notifications_tab", "Notifications", Icons.Filled.Notifications, Icons.Outlined.Notifications, 1),
  MENU("menu", "Menu", Icons.Filled.Menu, Icons.Outlined.Menu)
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      HobbyCircleApp()
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HobbyCircleApp(
  viewModel: HobbyViewModel = viewModel()
) {
  val userProfile by viewModel.userProfile.collectAsState()
  val syncState by viewModel.syncState.collectAsState()
  val posts by viewModel.posts.collectAsState()
  val stories by viewModel.stories.collectAsState()
  val commentsMap by viewModel.comments.collectAsState()
  val friends by viewModel.friends.collectAsState()
  val activeStory by viewModel.activeStory.collectAsState()
  val activeCommentsPost by viewModel.activeCommentsPost.collectAsState()
  val isCreatePostOpen by viewModel.isCreatePostOpen.collectAsState()

  val groups by viewModel.groups.collectAsState()
  val events by viewModel.events.collectAsState()
  val messages by viewModel.allMessages.collectAsState()
  val documents by viewModel.documents.collectAsState()
  val milestones by viewModel.milestones.collectAsState()
  val notifications by viewModel.notifications.collectAsState()
  val unreadNotificationsCount by viewModel.unreadNotificationsCount.collectAsState()
  val discoveryState by viewModel.discoveryState.collectAsState()
  val videoLoungeState by viewModel.videoLoungeState.collectAsState()

  val systemInDark = isSystemInDarkTheme()
  val isDarkTheme = when (userProfile.darkModePreference) {
    "DARK" -> true
    "LIGHT" -> false
    else -> systemInDark
  }

  var showOnboardingDialog by remember { mutableStateOf(false) }
  var showCreateEventModal by remember { mutableStateOf(false) }

  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  val isTopLevelDestination = FacebookTab.values().any { it.route == currentRoute }

  HobbyCircleTheme(darkTheme = isDarkTheme) {
    Scaffold(
      topBar = {
        if (isTopLevelDestination) {
          Column {
            // 1. Facebook Header: Logo + Search + Create + Messenger
            FacebookTopBar(
              unreadMessengerCount = 2,
              unreadNotificationsCount = unreadNotificationsCount,
              onCreateClick = { viewModel.setCreatePostOpen(true) },
              onSearchClick = { navController.navigate("discovery") },
              onMessengerClick = { navController.navigate("chats") },
              onNotificationsClick = { navController.navigate(FacebookTab.NOTIFICATIONS.route) }
            )

            // 2. Facebook Top Navigation Tab Bar (Iconic 6 Tabs)
            val selectedTabIndex = FacebookTab.values().indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
            TabRow(
              selectedTabIndex = selectedTabIndex,
              containerColor = MaterialTheme.colorScheme.surface,
              contentColor = FacebookBlue,
              indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                  TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = FacebookBlue,
                    height = 3.dp
                  )
                }
              },
              divider = {
                HorizontalDivider(thickness = 0.8.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
              },
              modifier = Modifier.fillMaxWidth()
            ) {
              FacebookTab.values().forEachIndexed { index, tab ->
                val isSelected = selectedTabIndex == index
                Tab(
                  selected = isSelected,
                  onClick = {
                    if (currentRoute != tab.route) {
                      navController.navigate(tab.route) {
                        popUpTo(FacebookTab.FEED.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                      }
                    }
                  },
                  icon = {
                    BadgedBox(
                      badge = {
                        if (tab == FacebookTab.NOTIFICATIONS && unreadNotificationsCount > 0) {
                          Badge(containerColor = MaterialTheme.colorScheme.error) {
                            Text("$unreadNotificationsCount", fontSize = 9.sp)
                          }
                        } else if (tab == FacebookTab.FRIENDS && friends.any { it.hasPendingRequest }) {
                          Badge(containerColor = MaterialTheme.colorScheme.error) {
                            Text("${friends.count { it.hasPendingRequest }}", fontSize = 9.sp)
                          }
                        }
                      }
                    ) {
                      Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.label,
                        tint = if (isSelected) FacebookBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                      )
                    }
                  },
                  modifier = Modifier.testTag("tab_${tab.route}")
                )
              }
            }

            // Offline / Cloud Sync Status Banner
            SyncStatusBanner(
              syncState = syncState,
              onSyncClick = { viewModel.triggerManualSync() }
            )
          }
        }
      }
    ) { innerPadding ->
      NavHost(
        navController = navController,
        startDestination = FacebookTab.FEED.route,
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        // --- 1. Facebook Feed (Home) ---
        composable(FacebookTab.FEED.route) {
          FacebookFeedScreen(
            posts = posts,
            stories = stories,
            userProfile = userProfile,
            syncState = syncState,
            onReactToPost = { postId, reaction -> viewModel.reactToPost(postId, reaction) },
            onCommentClick = { post -> viewModel.openComments(post) },
            onShareClick = { post -> viewModel.sharePost(post.id) },
            onSendClick = { post -> navController.navigate("chat/group_1/${post.authorName}") },
            onEventRsvp = { eventId, status -> viewModel.updateEventRsvp(eventId, status) },
            onStoryClick = { story -> viewModel.openStory(story) },
            onCreateStoryClick = {
              viewModel.createFacebookPost(
                content = "Shared a new story snapshot with the community! 🌟",
                mediaType = "PHOTO_GRID",
                mediaCaption = "Story Snapshot from Workshop"
              )
            },
            onOpenComposer = { viewModel.setCreatePostOpen(true) },
            onOpenLiveStage = { navController.navigate("video_lounge") },
            onOpenRadar = { navController.navigate("discovery") },
            onTriggerSync = { viewModel.triggerManualSync() }
          )
        }

        // --- 2. Friends Tab ---
        composable(FacebookTab.FRIENDS.route) {
          FacebookFriendsScreen(
            friends = friends,
            onToggleFriend = { friendId -> viewModel.toggleFriend(friendId) },
            onAcceptRequest = { friendId -> viewModel.acceptFriendRequest(friendId) },
            onMessageClick = { friend -> navController.navigate("chat/${friend.id}/${friend.name}") }
          )
        }

        // --- 3. Groups Tab ---
        composable(FacebookTab.GROUPS.route) {
          GroupsScreen(
            groups = groups,
            onGroupClick = { navController.navigate("group_detail/$it") },
            onToggleMembership = { gId, curr -> viewModel.toggleGroupMembership(gId, curr) },
            onOpenGroupChat = { navController.navigate("chat/$it/${groups.find { g -> g.id == it }?.name ?: "Guild"}") },
            onOpenGroupDocs = {
              val doc = documents.find { d -> d.groupId == it }
              if (doc != null) {
                navController.navigate("doc_editor/${doc.id}")
              } else {
                navController.navigate("workspace")
              }
            },
            onCreateGroup = { name, cat, desc, isPriv, loc, tags, rules, emoji ->
              viewModel.createGroup(name, cat, desc, isPriv, loc, tags, rules, emoji)
            }
          )
        }

        // --- 4. Events Tab ---
        composable(FacebookTab.EVENTS.route) {
          EventsScreen(
            events = events,
            groups = groups,
            onEventClick = { navController.navigate("event_detail/$it") },
            onUpdateRsvp = { eId, status -> viewModel.updateEventRsvp(eId, status) },
            onCreateEventClick = { showCreateEventModal = true }
          )
        }

        // --- 5. Notifications Tab ---
        composable(FacebookTab.NOTIFICATIONS.route) {
          NotificationsScreen(
            notifications = notifications,
            onBackClick = { navController.popBackStack() },
            onMarkRead = { viewModel.markNotificationRead(it) },
            onMarkAllRead = { viewModel.markAllNotificationsRead() }
          )
        }

        // --- 6. Menu Tab ---
        composable(FacebookTab.MENU.route) {
          FacebookMenuScreen(
            userProfile = userProfile,
            syncState = syncState,
            onNavigateToProfile = { navController.navigate("profile") },
            onNavigateToRadar = { navController.navigate("discovery") },
            onNavigateToGroups = { navController.navigate(FacebookTab.GROUPS.route) },
            onNavigateToEvents = { navController.navigate(FacebookTab.EVENTS.route) },
            onNavigateToCollab = { navController.navigate("workspace") },
            onNavigateToLiveStage = { navController.navigate("video_lounge") },
            onNavigateToMessenger = { navController.navigate("chats") },
            onNavigateToNotifications = { navController.navigate(FacebookTab.NOTIFICATIONS.route) },
            onToggleDarkMode = { viewModel.setDarkModePreference(it) },
            onToggleOfflineMode = { viewModel.toggleOfflineMode() },
            onTriggerSync = { viewModel.triggerManualSync() },
            onOpenOnboarding = { showOnboardingDialog = true }
          )
        }

        // --- Additional Sub-screens ---

        // Radar Discovery Map
        composable("discovery") {
          MapDiscoveryScreen(
            groups = groups,
            events = events,
            discoveryState = discoveryState,
            onCategorySelected = { viewModel.setDiscoveryCategory(it) },
            onSearchQueryChanged = { viewModel.setDiscoverySearch(it) },
            onRadiusSelected = { viewModel.setDiscoveryRadius(it) },
            onPinGroupSelected = { viewModel.selectMapPinGroup(it) },
            onPinEventSelected = { viewModel.selectMapPinEvent(it) },
            onToggleGroupMembership = { gId, curr -> viewModel.toggleGroupMembership(gId, curr) },
            onUpdateEventRsvp = { eId, status -> viewModel.updateEventRsvp(eId, status) },
            onNavigateToGroupDetail = { navController.navigate("group_detail/$it") },
            onNavigateToEventDetail = { navController.navigate("event_detail/$it") },
            onCreateEventClick = { showCreateEventModal = true }
          )
        }

        // Guild / Group Detail
        composable(
          route = "group_detail/{groupId}",
          arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->
          val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
          val group = groups.find { it.id == groupId }
          GroupDetailScreen(
            group = group,
            events = events,
            documents = documents,
            onBackClick = { navController.popBackStack() },
            onToggleMembership = { gId, curr -> viewModel.toggleGroupMembership(gId, curr) },
            onOpenChat = { navController.navigate("chat/$it/${group?.name ?: "Guild"}") },
            onOpenDoc = { navController.navigate("doc_editor/$it") },
            onOpenEvent = { navController.navigate("event_detail/$it") },
            onOpenVideoLounge = { navController.navigate("video_lounge") },
            onEndorseOrganizer = { viewModel.endorseOrganizer(2) }
          )
        }

        // Event Detail
        composable(
          route = "event_detail/{eventId}",
          arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
          val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
          val event = events.find { it.id == eventId }
          EventDetailScreen(
            event = event,
            onBackClick = { navController.popBackStack() },
            onUpdateRsvp = { status -> viewModel.updateEventRsvp(eventId, status) },
            onToggleEquipment = { itemId -> viewModel.toggleEquipmentItem(eventId, itemId) },
            onOpenVideoLounge = { navController.navigate("video_lounge") },
            onEndorseOrganizer = { viewModel.endorseOrganizer(2) }
          )
        }

        // Collaborative Workspace
        composable("workspace") {
          CollabWorkspaceScreen(
            documents = documents,
            milestones = milestones,
            groups = groups,
            onSelectDocument = { navController.navigate("doc_editor/$it") },
            onToggleMilestone = { id, isComp -> viewModel.toggleMilestone(id, isComp) },
            onOpenVideoLounge = { navController.navigate("video_lounge") },
            onCreateDocument = { gId, gName, title, content -> viewModel.createDocument(gId, gName, title, content) },
            onCreateMilestone = { gId, title, dueDate, member -> viewModel.createMilestone(gId, title, dueDate, member) }
          )
        }

        // Document Editor
        composable(
          route = "doc_editor/{docId}",
          arguments = listOf(navArgument("docId") { type = NavType.StringType })
        ) { backStackEntry ->
          val docId = backStackEntry.arguments?.getString("docId") ?: ""
          val doc = documents.find { it.id == docId }
          DocumentEditorScreen(
            document = doc,
            onBackClick = { navController.popBackStack() },
            onSaveContent = { id, content -> viewModel.updateDocument(id, content) }
          )
        }

        // Video Lounge
        composable("video_lounge") {
          VideoLoungeScreen(
            state = videoLoungeState,
            onBackClick = { navController.popBackStack() },
            onToggleMute = { viewModel.toggleMute() },
            onToggleCamera = { viewModel.toggleCamera() },
            onToggleScreenShare = { viewModel.toggleScreenShare() },
            onSendReaction = { viewModel.sendReaction(it) }
          )
        }

        // Encrypted Chats / Messenger List
        composable("chats") {
          ChatListScreen(
            groups = groups,
            messages = messages,
            onSelectConversation = { convId, title ->
              navController.navigate("chat/$convId/$title")
            }
          )
        }

        // Chat Conversation
        composable(
          route = "chat/{conversationId}/{title}",
          arguments = listOf(
            navArgument("conversationId") { type = NavType.StringType },
            navArgument("title") { type = NavType.StringType }
          )
        ) { backStackEntry ->
          val convId = backStackEntry.arguments?.getString("conversationId") ?: ""
          val title = backStackEntry.arguments?.getString("title") ?: "Chat"
          ChatConversationScreen(
            conversationId = convId,
            title = title,
            messages = messages,
            onBackClick = { navController.popBackStack() },
            onSendMessage = { text, mediaType, caption ->
              viewModel.sendMessage(convId, text, mediaType, caption)
            }
          )
        }

        // Profile & Settings
        composable("profile") {
          ProfileScreen(
            userProfile = userProfile,
            syncState = syncState,
            onBackClick = { navController.popBackStack() },
            onSetDarkMode = { viewModel.setDarkModePreference(it) },
            onToggleOfflineMode = { viewModel.toggleOfflineMode() },
            onTriggerSync = { viewModel.triggerManualSync() },
            onShowOnboarding = { showOnboardingDialog = true },
            onEndorseOrganizer = { viewModel.endorseOrganizer(2) }
          )
        }
      }
    }

    // Modal Create Post Dialog
    if (isCreatePostOpen) {
      CreatePostDialog(
        userAvatar = userProfile.avatarEmoji,
        userName = userProfile.name,
        groups = groups,
        onDismiss = { viewModel.setCreatePostOpen(false) },
        onPublishPost = { content, gId, gName, feeling, loc, mediaType, mediaCaption, privacy ->
          viewModel.createFacebookPost(
            content = content,
            groupId = gId,
            groupName = gName,
            feelingOrActivity = feeling,
            locationTag = loc,
            mediaType = mediaType,
            mediaCaption = mediaCaption,
            privacy = privacy
          )
        }
      )
    }

    // Modal Story Viewer Dialog
    activeStory?.let { story ->
      StoryViewerDialog(
        story = story,
        onDismiss = { viewModel.closeStory() },
        onSendReaction = { reaction ->
          viewModel.sendMessage(
            convId = "group_1",
            text = "Story reaction to ${story.authorName}: $reaction"
          )
        }
      )
    }

    // Modal Comments Bottom Sheet
    activeCommentsPost?.let { post ->
      FacebookCommentsSheet(
        post = post,
        comments = commentsMap[post.id] ?: emptyList(),
        userAvatar = userProfile.avatarEmoji,
        onDismiss = { viewModel.closeComments() },
        onAddComment = { text -> viewModel.addComment(post.id, text) },
        onLikeComment = { commentId -> viewModel.toggleLikeComment(post.id, commentId) }
      )
    }

    // Modal Create Event Dialog
    if (showCreateEventModal) {
      CreateEventDialog(
        groups = groups,
        onDismiss = { showCreateEventModal = false },
        onCreate = { groupId, title, category, description, date, time, location, maxAttendees, meetingType, eqList ->
          viewModel.createEvent(
            groupId = groupId,
            title = title,
            category = category,
            description = description,
            dateFormatted = date,
            timeFormatted = time,
            locationName = location,
            maxAttendees = maxAttendees,
            meetingType = meetingType,
            equipmentList = eqList
          )
          showCreateEventModal = false
        }
      )
    }

    // Onboarding Tutorial Dialog
    if (showOnboardingDialog) {
      OnboardingTutorialDialog(
        onDismiss = { showOnboardingDialog = false }
      )
    }
  }
}
