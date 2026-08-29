package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.model.FacebookComment
import com.example.data.model.FacebookPost
import com.example.ui.theme.FacebookBlue
import com.example.ui.theme.ReactionLikeBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacebookCommentsSheet(
  post: FacebookPost,
  comments: List<FacebookComment>,
  userAvatar: String,
  onDismiss: () -> Unit,
  onAddComment: (String) -> Unit,
  onLikeComment: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var commentInput by remember { mutableStateOf("") }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = modifier.testTag("comments_bottom_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.85f)
        .padding(bottom = 16.dp)
    ) {
      // Sheet Header
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        // Reactions summary in header
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "👍 ❤️", fontSize = 16.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${post.reactionsCount} reactions",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Text(
          text = "Comments",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
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

      HorizontalDivider(thickness = 0.8.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

      // Comments List
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        if (comments.isEmpty()) {
          item {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp)
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "💬", fontSize = 42.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = "No comments yet",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Be the first to share your thoughts on this project!",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        } else {
          items(comments, key = { it.id }) { comment ->
            CommentBubbleItem(
              comment = comment,
              onLikeClick = { onLikeComment(comment.id) }
            )
          }
        }
      }

      HorizontalDivider(thickness = 0.8.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

      // Bottom Comment Input Bar
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 8.dp)
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Text(text = userAvatar, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.width(10.dp))

        OutlinedTextField(
          value = commentInput,
          onValueChange = { commentInput = it },
          placeholder = { Text("Write a comment...", fontSize = 14.sp) },
          trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(
                onClick = { commentInput += " 🔥" },
                modifier = Modifier.size(30.dp)
              ) {
                Icon(
                  imageVector = Icons.Outlined.Mood,
                  contentDescription = "Add Emoji",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              if (commentInput.isNotBlank()) {
                IconButton(
                  onClick = {
                    onAddComment(commentInput)
                    commentInput = ""
                  },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Comment",
                    tint = FacebookBlue
                  )
                }
              }
            }
          },
          shape = RoundedCornerShape(22.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
          ),
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
        )
      }
    }
  }
}

@Composable
fun CommentBubbleItem(
  comment: FacebookComment,
  onLikeClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Start
  ) {
    // Author Avatar
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(34.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
      Text(text = comment.authorAvatar, fontSize = 17.sp)
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column {
      // Facebook-style Comment Pill Bubble
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
        modifier = Modifier.widthIn(max = 280.dp)
      ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
          Text(
            text = comment.authorName,
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = comment.text,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      // Actions below comment: Time, Like, Reply, Like Counter Badge
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
      ) {
        Text(
          text = comment.timestampFormatted,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
          text = if (comment.isLiked) "Liked" else "Like",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          ),
          color = if (comment.isLiked) ReactionLikeBlue else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.clickable { onLikeClick() }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
          text = "Reply",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          ),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.clickable { /* reply */ }
        )

        if (comment.likesCount > 0) {
          Spacer(modifier = Modifier.width(10.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(MaterialTheme.colorScheme.surface)
              .padding(horizontal = 4.dp, vertical = 2.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ThumbUp,
              contentDescription = null,
              tint = ReactionLikeBlue,
              modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "${comment.likesCount}",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}
