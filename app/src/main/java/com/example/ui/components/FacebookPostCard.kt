package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.model.EventRsvpStatus
import com.example.data.model.FacebookPost
import com.example.data.model.PostPrivacy
import com.example.data.model.ReactionType
import com.example.ui.theme.FacebookBlue
import com.example.ui.theme.ReactionLikeBlue
import com.example.ui.theme.ReactionLoveRed

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FacebookPostCard(
  post: FacebookPost,
  onReact: (ReactionType) -> Unit,
  onCommentClick: () -> Unit,
  onShareClick: () -> Unit,
  onSendClick: () -> Unit,
  onEventRsvpClick: ((String, EventRsvpStatus) -> Unit)? = null,
  onAuthorClick: (() -> Unit)? = null,
  onGroupClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var showReactionPicker by remember { mutableStateOf(false) }
  var showMenu by remember { mutableStateOf(false) }

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier
      .fillMaxWidth()
      .testTag("post_card_${post.id}")
  ) {
    Column(modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)) {

      // --- Header: Avatar, Name, Group Tag, Timestamp, Privacy & Menu ---
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp)
      ) {
        // Author Avatar with Online dot / Verified Ring
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onAuthorClick?.invoke() }
        ) {
          Text(text = post.authorAvatar, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = post.authorName,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              ),
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.clickable { onAuthorClick?.invoke() }
            )

            if (post.isVerifiedAuthor) {
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Verified Organizer",
                tint = FacebookBlue,
                modifier = Modifier.size(15.dp)
              )
            }

            if (!post.groupName.isNullOrEmpty()) {
              Text(
                text = " ▶ ",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = post.groupName,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 14.sp
                ),
                color = FacebookBlue,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { onGroupClick?.invoke() }
              )
            }
          }

          // Subtitle: Feeling / Activity, Timestamp, Privacy Icon
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 2.dp)
          ) {
            if (!post.feelingOrActivity.isNullOrEmpty()) {
              Text(
                text = "${post.feelingOrActivity} • ",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Text(
              text = post.timestampFormatted,
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
              text = " • ",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val (privacyIcon, privacyDesc) = when (post.privacy) {
              PostPrivacy.PUBLIC -> Icons.Default.Public to "Public"
              PostPrivacy.FRIENDS -> Icons.Outlined.Groups to "Friends"
              PostPrivacy.GROUP_ONLY -> Icons.Default.Lock to "Guild Only"
            }

            Icon(
              imageVector = privacyIcon,
              contentDescription = privacyDesc,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(13.dp)
            )

            if (!post.locationTag.isNullOrEmpty()) {
              Text(
                text = " • 📍 ${post.locationTag}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        // More options button
        Box {
          IconButton(
            onClick = { showMenu = true },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.MoreHoriz,
              contentDescription = "Post Options",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
          ) {
            DropdownMenuItem(
              text = { Text("Save Post") },
              onClick = { showMenu = false }
            )
            DropdownMenuItem(
              text = { Text("Copy Link") },
              onClick = { showMenu = false }
            )
            DropdownMenuItem(
              text = { Text("Turn on notifications for this post") },
              onClick = { showMenu = false }
            )
            DropdownMenuItem(
              text = { Text("Hide Post") },
              onClick = { showMenu = false }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // --- Post Content Text & Hashtags ---
      if (post.content.isNotEmpty()) {
        Text(
          text = post.content,
          style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 15.sp,
            lineHeight = 21.sp
          ),
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(horizontal = 14.dp)
        )
      }

      if (post.hashtags.isNotEmpty()) {
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.padding(horizontal = 14.dp)
        ) {
          post.hashtags.forEach { tag ->
            Text(
              text = tag,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              ),
              color = FacebookBlue
            )
          }
        }
      }

      // --- Post Media / Attached Cards ---
      if (post.mediaType != "NONE") {
        Spacer(modifier = Modifier.height(10.dp))
        FacebookPostMedia(post = post, onEventRsvpClick = onEventRsvpClick)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // --- Social Stats Counter Bar (Reactions, Comments, Shares) ---
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 4.dp)
      ) {
        // Reactions pill
        if (post.reactionsCount > 0) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { showReactionPicker = !showReactionPicker }
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy((-4).dp)) {
              post.topReactions.take(3).forEach { emoji ->
                Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                      when (emoji) {
                        "❤️" -> ReactionLoveRed
                        "🔥" -> Color(0xFFFF5722)
                        else -> ReactionLikeBlue
                      }
                    )
                ) {
                  Text(text = emoji, fontSize = 11.sp)
                }
              }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "${post.reactionsCount}",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        } else {
          Spacer(modifier = Modifier.width(1.dp))
        }

        // Comments & Shares count
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (post.commentsCount > 0) {
            Text(
              text = "${post.commentsCount} comments",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.clickable { onCommentClick() }
            )
          }

          if (post.commentsCount > 0 && post.sharesCount > 0) {
            Text(
              text = " • ",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          if (post.sharesCount > 0) {
            Text(
              text = "${post.sharesCount} shares",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.clickable { onShareClick() }
            )
          }
        }
      }

      HorizontalDivider(
        thickness = 0.8.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
      )

      // --- Reaction Picker Bubble Floating Popup ---
      Box(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.zIndex(10f)) {
          AnimatedVisibility(
            visible = showReactionPicker,
            enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.padding(start = 14.dp, bottom = 44.dp)
          ) {
            ReactionPickerBar(
              onSelectReaction = { reaction ->
                onReact(reaction)
                showReactionPicker = false
              }
            )
          }
        }

        // --- Facebook Action Bar: Like, Comment, Share, Send ---
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceAround,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
        ) {
          // Like / Reaction Button
          val isReacted = post.myReaction != null
          val reactionColor = post.myReaction?.colorHex?.let { Color(it) } ?: MaterialTheme.colorScheme.onSurfaceVariant

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .clickable {
                if (post.myReaction == null) {
                  onReact(ReactionType.LIKE)
                } else {
                  onReact(post.myReaction)
                }
              }
              .padding(vertical = 8.dp)
              .testTag("like_button_${post.id}")
          ) {
            if (isReacted) {
              Text(
                text = post.myReaction!!.emoji,
                fontSize = 18.sp
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = post.myReaction!!.label,
                style = MaterialTheme.typography.labelLarge.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                ),
                color = reactionColor
              )
            } else {
              Icon(
                imageVector = Icons.Outlined.ThumbUp,
                contentDescription = "Like",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = "Like",
                style = MaterialTheme.typography.labelLarge.copy(
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 13.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Comment Button
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .clickable { onCommentClick() }
              .padding(vertical = 8.dp)
              .testTag("comment_button_${post.id}")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.Chat,
              contentDescription = "Comment",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "Comment",
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              ),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Share Button
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .clickable { onShareClick() }
              .padding(vertical = 8.dp)
              .testTag("share_button_${post.id}")
          ) {
            Icon(
              imageVector = Icons.Outlined.Share,
              contentDescription = "Share",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "Share",
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              ),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Send Button (Encrypted Messenger)
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .clickable { onSendClick() }
              .padding(vertical = 8.dp)
              .testTag("send_button_${post.id}")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.Send,
              contentDescription = "Send Message",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "Send",
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              ),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}

@Composable
fun ReactionPickerBar(
  onSelectReaction: (ReactionType) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(24.dp),
    color = MaterialTheme.colorScheme.surface,
    shadowElevation = 6.dp,
    border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = modifier.shadow(6.dp, RoundedCornerShape(24.dp))
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
      ReactionType.values().forEach { reaction ->
        Text(
          text = reaction.emoji,
          fontSize = 24.sp,
          modifier = Modifier
            .clip(CircleShape)
            .clickable { onSelectReaction(reaction) }
            .padding(4.dp)
        )
      }
    }
  }
}

@Composable
fun FacebookPostMedia(
  post: FacebookPost,
  onEventRsvpClick: ((String, EventRsvpStatus) -> Unit)? = null
) {
  when (post.mediaType) {
    "EVENT_CARD" -> {
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(12.dp)
        ) {
          // Big Date Box (Facebook style)
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(54.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(FacebookBlue)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "EVENT",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp
                ),
                color = Color.White
              )
              Text(
                text = "📅",
                fontSize = 20.sp
              )
            }
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = post.attachedEventDate ?: "Upcoming Community Meetup",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              ),
              color = FacebookBlue
            )
            Text(
              text = post.attachedEventTitle ?: "Featured Guild Session",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              ),
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
              .clickable {
                post.attachedEventId?.let { onEventRsvpClick?.invoke(it, EventRsvpStatus.GOING) }
              }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Outlined.Event,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "RSVP",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        }
      }
    }

    "VIDEO_STAGE" -> {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .background(
            Brush.verticalGradient(
              colors = listOf(Color(0xFF1E1B4B), Color(0xFF312E81))
            )
          )
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(16.dp)
        ) {
          Text(text = "🎙️ Live Audio & Video Stage", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
          Spacer(modifier = Modifier.height(6.dp))
          Text(text = post.mediaCaption, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
          Spacer(modifier = Modifier.height(12.dp))
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = FacebookBlue,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
          ) {
            Text(text = "🔴 Join Live Broadcast", color = Color.White, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
          }
        }
      }
    }

    else -> {
      // Photo / Blueprint Media Card
      Box(
        contentAlignment = Alignment.BottomStart,
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
          .background(
            Brush.linearGradient(
              colors = listOf(Color(post.mediaColorHex), Color(post.mediaColorHex).copy(alpha = 0.7f))
            )
          )
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
        ) {
          Text(text = post.mediaEmoji, fontSize = 64.sp)
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
          Text(
            text = post.mediaCaption,
            style = MaterialTheme.typography.bodySmall.copy(
              fontWeight = FontWeight.Medium,
              fontSize = 12.sp
            ),
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}
