package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.HobbyGroup
import com.example.data.model.PostPrivacy
import com.example.ui.theme.FacebookBlue
import com.example.ui.theme.ReactionLoveRed
import com.example.ui.theme.SecondaryLight

@Composable
fun FacebookComposerCard(
  userAvatar: String,
  userName: String,
  onClick: () -> Unit,
  onLiveClick: () -> Unit,
  onPhotoClick: () -> Unit,
  onFeelingClick: () -> Unit,
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
      .testTag("feed_composer_card")
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Top Row: Avatar + "What's on your mind?" Input pill
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Text(text = userAvatar, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
          contentAlignment = Alignment.CenterStart,
          modifier = Modifier
            .weight(1f)
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .clickable { onClick() }
            .padding(horizontal = 16.dp)
        ) {
          Text(
            text = "What's on your mind, ${userName.split(" ").firstOrNull() ?: "there"}?",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      HorizontalDivider(
        thickness = 0.8.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        modifier = Modifier.padding(vertical = 10.dp)
      )

      // Bottom Row: [Live Video] [Photo/video] [Feeling/activity]
      Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        // Live Video
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onLiveClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Videocam,
            contentDescription = "Live Video",
            tint = ReactionLoveRed,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Live Stage",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        // Photo/Video
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onPhotoClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Image,
            contentDescription = "Photo/Video",
            tint = SecondaryLight,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Photo/video",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        // Feeling/Activity
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onFeelingClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Mood,
            contentDescription = "Feeling/activity",
            tint = Color(0xFFF59E0B),
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Feeling",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreatePostDialog(
  userAvatar: String,
  userName: String,
  groups: List<HobbyGroup>,
  onDismiss: () -> Unit,
  onPublishPost: (
    content: String,
    groupId: String?,
    groupName: String?,
    feeling: String?,
    location: String?,
    mediaType: String,
    mediaCaption: String,
    privacy: PostPrivacy
  ) -> Unit
) {
  var content by remember { mutableStateOf("") }
  var selectedPrivacy by remember { mutableStateOf(PostPrivacy.PUBLIC) }
  var selectedGroup by remember { mutableStateOf<HobbyGroup?>(null) }
  var feeling by remember { mutableStateOf<String?>(null) }
  var locationTag by remember { mutableStateOf<String?>(null) }
  var mediaType by remember { mutableStateOf("NONE") }
  var mediaCaption by remember { mutableStateOf("") }

  var showPrivacyMenu by remember { mutableStateOf(false) }
  var showGroupMenu by remember { mutableStateOf(false) }
  var showFeelingPicker by remember { mutableStateOf(false) }

  val feelingsList = listOf(
    "🎨 feeling creative",
    "🛠️ building a project",
    "🌿 gardening",
    "🎯 playing tabletop",
    "🎛️ sound designing",
    "🔭 stargazing",
    "🔥 feeling motivated"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surface,
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier.padding(16.dp)
      ) {
        // Dialog Header: Title & Close Button
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Spacer(modifier = Modifier.size(24.dp))
          Text(
            text = "Create post",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        HorizontalDivider(
          thickness = 0.8.dp,
          color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
          modifier = Modifier.padding(vertical = 12.dp)
        )

        // User Avatar + Name + Privacy Pills
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Text(text = userAvatar, fontSize = 22.sp)
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = userName,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              // Privacy Selector Pill
              Box {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant,
                  modifier = Modifier
                    .clickable { showPrivacyMenu = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    val (icon, label) = when (selectedPrivacy) {
                      PostPrivacy.PUBLIC -> Icons.Default.Public to "Public"
                      PostPrivacy.FRIENDS -> Icons.Outlined.Groups to "Friends"
                      PostPrivacy.GROUP_ONLY -> Icons.Default.Lock to "Guild Only"
                    }
                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                  }
                }

                DropdownMenu(
                  expanded = showPrivacyMenu,
                  onDismissRequest = { showPrivacyMenu = false }
                ) {
                  DropdownMenuItem(
                    text = { Text("Public 🌍") },
                    onClick = {
                      selectedPrivacy = PostPrivacy.PUBLIC
                      showPrivacyMenu = false
                    }
                  )
                  DropdownMenuItem(
                    text = { Text("Friends Only 👥") },
                    onClick = {
                      selectedPrivacy = PostPrivacy.FRIENDS
                      showPrivacyMenu = false
                    }
                  )
                  DropdownMenuItem(
                    text = { Text("Guild Only 🔒") },
                    onClick = {
                      selectedPrivacy = PostPrivacy.GROUP_ONLY
                      showPrivacyMenu = false
                    }
                  )
                }
              }

              // Post in Group Pill
              Box {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant,
                  modifier = Modifier
                    .clickable { showGroupMenu = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = selectedGroup?.name?.take(16) ?: "Post in Feed",
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                      color = FacebookBlue
                    )
                  }
                }

                DropdownMenu(
                  expanded = showGroupMenu,
                  onDismissRequest = { showGroupMenu = false }
                ) {
                  DropdownMenuItem(
                    text = { Text("General Public Feed") },
                    onClick = {
                      selectedGroup = null
                      showGroupMenu = false
                    }
                  )
                  groups.forEach { g ->
                    DropdownMenuItem(
                      text = { Text("${g.iconEmoji} ${g.name}") },
                      onClick = {
                        selectedGroup = g
                        showGroupMenu = false
                      }
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Text Input Field
        OutlinedTextField(
          value = content,
          onValueChange = { content = it },
          placeholder = {
            Text(
              text = "What's on your mind? Share your hobby progress, ask for advice, or post an update...",
              fontSize = 16.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
          ),
          minLines = 4,
          maxLines = 8,
          modifier = Modifier.fillMaxWidth()
        )

        // Selected Feeling or Location Pills
        if (feeling != null || locationTag != null || mediaType != "NONE") {
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(vertical = 6.dp)
          ) {
            if (feeling != null) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(2.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                  Text(text = feeling!!, style = MaterialTheme.typography.labelSmall)
                  Spacer(modifier = Modifier.width(4.dp))
                  Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(12.dp).clickable { feeling = null })
                }
              }
            }

            if (locationTag != null) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.padding(2.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                  Text(text = "📍 $locationTag", style = MaterialTheme.typography.labelSmall)
                  Spacer(modifier = Modifier.width(4.dp))
                  Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(12.dp).clickable { locationTag = null })
                }
              }
            }

            if (mediaType != "NONE") {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier.padding(2.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                  Text(text = "📷 $mediaCaption", style = MaterialTheme.typography.labelSmall)
                  Spacer(modifier = Modifier.width(4.dp))
                  Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(12.dp).clickable { mediaType = "NONE"; mediaCaption = "" })
                }
              }
            }
          }
        }

        // Add to your post toolbar
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = androidx.compose.foundation.BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
          ) {
            Text(
              text = "Add to your post",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              // Photo attachment shortcut
              IconButton(
                onClick = {
                  mediaType = "PHOTO_GRID"
                  mediaCaption = "Hobby Workshop Snapshot (Room Cached)"
                },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(imageVector = Icons.Outlined.Image, contentDescription = "Add Photo", tint = SecondaryLight)
              }

              // Feeling selector shortcut
              IconButton(
                onClick = { showFeelingPicker = !showFeelingPicker },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(imageVector = Icons.Outlined.Mood, contentDescription = "Add Feeling", tint = Color(0xFFF59E0B))
              }

              // Location tag shortcut
              IconButton(
                onClick = { locationTag = "Makerspace Downtown, Studio 4" },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = "Tag Location", tint = ReactionLoveRed)
              }
            }
          }
        }

        if (showFeelingPicker) {
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(bottom = 10.dp)
          ) {
            feelingsList.forEach { f ->
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                  .clickable {
                    feeling = f
                    showFeelingPicker = false
                  }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(text = f, style = MaterialTheme.typography.labelSmall)
              }
            }
          }
        }

        // Post Primary Button
        Button(
          onClick = {
            if (content.isNotBlank() || mediaType != "NONE") {
              onPublishPost(
                content,
                selectedGroup?.id,
                selectedGroup?.name,
                feeling,
                locationTag,
                mediaType,
                mediaCaption,
                selectedPrivacy
              )
            }
          },
          enabled = content.isNotBlank() || mediaType != "NONE",
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = FacebookBlue),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("publish_post_button")
        ) {
          Text(
            text = "Post",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
        }
      }
    }
  }
}
