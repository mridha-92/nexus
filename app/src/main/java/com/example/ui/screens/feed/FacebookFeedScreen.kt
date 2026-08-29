package com.example.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventRsvpStatus
import com.example.data.model.FacebookPost
import com.example.data.model.FacebookStory
import com.example.data.model.ReactionType
import com.example.data.model.SyncState
import com.example.data.model.UserProfile
import com.example.ui.components.FacebookComposerCard
import com.example.ui.components.FacebookPostCard
import com.example.ui.components.FacebookStoriesRow
import com.example.ui.theme.FacebookBlue
import com.example.ui.theme.SecondaryLight

@Composable
fun FacebookFeedScreen(
  posts: List<FacebookPost>,
  stories: List<FacebookStory>,
  userProfile: UserProfile,
  syncState: SyncState,
  onReactToPost: (String, ReactionType) -> Unit,
  onCommentClick: (FacebookPost) -> Unit,
  onShareClick: (FacebookPost) -> Unit,
  onSendClick: (FacebookPost) -> Unit,
  onEventRsvp: (String, EventRsvpStatus) -> Unit,
  onStoryClick: (FacebookStory) -> Unit,
  onCreateStoryClick: () -> Unit,
  onOpenComposer: () -> Unit,
  onOpenLiveStage: () -> Unit,
  onOpenRadar: () -> Unit,
  onTriggerSync: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("facebook_feed_screen"),
    contentPadding = PaddingValues(bottom = 80.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // 1. "What's on your mind?" Composer Card
    item {
      FacebookComposerCard(
        userAvatar = userProfile.avatarEmoji,
        userName = userProfile.name,
        onClick = onOpenComposer,
        onLiveClick = onOpenLiveStage,
        onPhotoClick = onOpenComposer,
        onFeelingClick = onOpenComposer
      )
    }

    // 2. Stories Carousel Row
    item {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
      ) {
        FacebookStoriesRow(
          stories = stories,
          userAvatar = userProfile.avatarEmoji,
          onStoryClick = onStoryClick,
          onCreateStoryClick = onCreateStoryClick,
          modifier = Modifier.padding(vertical = 6.dp)
        )
      }
    }

    // 3. Quick Local Discovery & Offline Banner
    item {
      LocalRadarFeedBanner(
        location = userProfile.location,
        syncState = syncState,
        onOpenRadar = onOpenRadar,
        onTriggerSync = onTriggerSync
      )
    }

    // 4. Facebook Posts Feed
    items(posts, key = { it.id }) { post ->
      FacebookPostCard(
        post = post,
        onReact = { reaction -> onReactToPost(post.id, reaction) },
        onCommentClick = { onCommentClick(post) },
        onShareClick = { onShareClick(post) },
        onSendClick = { onSendClick(post) },
        onEventRsvpClick = onEventRsvp
      )
    }
  }
}

@Composable
fun LocalRadarFeedBanner(
  location: String,
  syncState: SyncState,
  onOpenRadar: () -> Unit,
  onTriggerSync: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 8.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      // Radar Discovery Shortcut
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.primaryContainer,
          modifier = Modifier.padding(4.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Sensors,
            contentDescription = "Radar",
            tint = FacebookBlue,
            modifier = Modifier
              .padding(6.dp)
              .size(18.dp)
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Text(
            text = "Nearby Hobbyists & Live Sessions",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Active around $location",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(16.dp),
        color = FacebookBlue,
        modifier = Modifier.padding(2.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .background(FacebookBlue)
            .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Explore,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Explore",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            ),
            color = Color.White
          )
        }
      }
    }
  }
}
