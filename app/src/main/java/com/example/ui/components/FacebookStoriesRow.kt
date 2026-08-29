package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.FacebookStory
import com.example.ui.theme.FacebookBlue
import kotlinx.coroutines.delay

@Composable
fun FacebookStoriesRow(
  stories: List<FacebookStory>,
  userAvatar: String,
  onStoryClick: (FacebookStory) -> Unit,
  onCreateStoryClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyRow(
    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier
      .fillMaxWidth()
      .testTag("stories_row")
  ) {
    // "Create Story" Card (Iconic Facebook style)
    item {
      CreateStoryCard(
        userAvatar = userAvatar,
        onClick = onCreateStoryClick
      )
    }

    // Friend / Hobbyist Stories
    items(stories.filter { !it.isMyStory }, key = { it.id }) { story ->
      StoryItemCard(
        story = story,
        onClick = { onStoryClick(story) }
      )
    }
  }
}

@Composable
fun CreateStoryCard(
  userAvatar: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .width(105.dp)
      .height(175.dp)
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .testTag("create_story_button")
  ) {
    Column(
      modifier = Modifier.fillMaxSize()
    ) {
      // Top Half: User Avatar background
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .fillMaxWidth()
          .height(115.dp)
          .background(MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Text(text = userAvatar, fontSize = 48.sp)
      }

      // Bottom Half with Overlapping (+) Blue Button
      Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .background(MaterialTheme.colorScheme.surface)
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxWidth()
        ) {
          // Plus Badge
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(FacebookBlue)
              .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Create Story",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Create story",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
          )
        }
      }
    }
  }
}

@Composable
fun StoryItemCard(
  story: FacebookStory,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .width(105.dp)
      .height(175.dp)
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .testTag("story_item_${story.id}")
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color(story.gradientStart),
              Color(story.gradientEnd)
            )
          )
        )
        .padding(8.dp)
    ) {
      // Big Central Emoji / Visual
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
      ) {
        Text(text = story.mediaEmoji, fontSize = 42.sp)
      }

      // Top Left: Author Avatar with Facebook Blue Story Ring
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .align(Alignment.TopStart)
          .size(36.dp)
          .clip(CircleShape)
          .border(
            width = if (!story.isSeen) 2.5.dp else 1.dp,
            color = if (!story.isSeen) FacebookBlue else Color.White.copy(alpha = 0.5f),
            shape = CircleShape
          )
          .background(Color.Black.copy(alpha = 0.3f))
      ) {
        Text(text = story.authorAvatar, fontSize = 18.sp)
      }

      // Bottom: Author Name
      Text(
        text = story.authorName,
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        ),
        color = Color.White,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.align(Alignment.BottomStart)
      )
    }
  }
}

@Composable
fun StoryViewerDialog(
  story: FacebookStory,
  onDismiss: () -> Unit,
  onSendReaction: (String) -> Unit
) {
  var progress by remember { mutableFloatStateOf(0f) }
  var replyText by remember { mutableStateOf("") }

  LaunchedEffect(story.id) {
    progress = 0f
    val duration = 5000L // 5 seconds per story
    val step = 50L
    val stepCount = duration / step
    for (i in 1..stepCount) {
      delay(step)
      progress = i.toFloat() / stepCount
    }
    onDismiss()
  }

  val animatedProgress by animateFloatAsState(
    targetValue = progress,
    animationSpec = tween(durationMillis = 50, easing = LinearEasing),
    label = "story_progress"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
    ) {
      // Main Story Canvas
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color(story.gradientStart),
                Color(story.gradientEnd)
              )
            )
          )
          .padding(24.dp)
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(text = story.mediaEmoji, fontSize = 90.sp)
          Spacer(modifier = Modifier.height(24.dp))
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.Black.copy(alpha = 0.45f),
            modifier = Modifier.padding(horizontal = 16.dp)
          ) {
            Text(
              text = story.caption,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                lineHeight = 24.sp
              ),
              color = Color.White,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(16.dp)
            )
          }
        }
      }

      // Top Overlay: Progress Bar, Author Info & Close
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 40.dp, start = 16.dp, end = 16.dp)
      ) {
        LinearProgressIndicator(
          progress = { animatedProgress },
          color = Color.White,
          trackColor = Color.White.copy(alpha = 0.3f),
          modifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.2f))
          ) {
            Text(text = story.authorAvatar, fontSize = 20.sp)
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = story.authorName,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
            Text(
              text = story.timestamp,
              style = MaterialTheme.typography.bodySmall,
              color = Color.White.copy(alpha = 0.7f)
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Story",
              tint = Color.White
            )
          }
        }
      }

      // Bottom Overlay: Reactions & Reply Input
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
      ) {
        // Quick Reaction Emojis (Facebook style)
        Row(
          horizontalArrangement = Arrangement.SpaceAround,
          modifier = Modifier.fillMaxWidth()
        ) {
          listOf("👍", "❤️", "🔥", "😆", "😮", "🌱").forEach { emoji ->
            Text(
              text = emoji,
              fontSize = 28.sp,
              modifier = Modifier
                .clip(CircleShape)
                .clickable {
                  onSendReaction(emoji)
                  onDismiss()
                }
                .padding(6.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Reply field
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = replyText,
            onValueChange = { replyText = it },
            placeholder = { Text("Reply to ${story.authorName}...", color = Color.White.copy(alpha = 0.6f)) },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = Color.White,
              unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
              focusedContainerColor = Color.Black.copy(alpha = 0.3f),
              unfocusedContainerColor = Color.Black.copy(alpha = 0.3f)
            ),
            modifier = Modifier.weight(1f)
          )

          Spacer(modifier = Modifier.width(8.dp))

          IconButton(
            onClick = {
              if (replyText.isNotBlank()) {
                onSendReaction("💬 $replyText")
                onDismiss()
              }
            }
          ) {
            Icon(
              imageVector = Icons.Default.Send,
              contentDescription = "Send Reply",
              tint = FacebookBlue
            )
          }
        }
      }
    }
  }
}
