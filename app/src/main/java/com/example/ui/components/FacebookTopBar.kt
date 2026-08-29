package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FacebookBlue
import com.example.ui.theme.ReactionLoveRed

@Composable
fun FacebookTopBar(
  unreadMessengerCount: Int = 2,
  unreadNotificationsCount: Int = 1,
  onCreateClick: () -> Unit,
  onSearchClick: () -> Unit,
  onMessengerClick: () -> Unit,
  onNotificationsClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    color = MaterialTheme.colorScheme.surface,
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
      // Big N Logo Badge + Nexus Wordmark Logo
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.testTag("app_logo")
      ) {
        // Iconic Big N Emblem Badge
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(FacebookBlue)
            .testTag("app_logo_badge")
        ) {
          Text(
            text = "N",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              fontSize = 22.sp,
              lineHeight = 22.sp
            ),
            color = Color.White
          )
        }

        Text(
          text = "nexus",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Black,
            fontSize = 26.sp,
            letterSpacing = (-1.0).sp
          ),
          color = FacebookBlue
        )
      }

      // Action Circular Buttons (Create +, Search 🔍, Messenger 💬)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Create (+) Button
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onCreateClick() }
            .testTag("topbar_create_button")
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Create Post",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp)
          )
        }

        // Search Button
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onSearchClick() }
            .testTag("topbar_search_button")
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp)
          )
        }

        // Messenger Button with Unread Badge
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onMessengerClick() }
            .testTag("topbar_messenger_button")
        ) {
          BadgedBox(
            badge = {
              if (unreadMessengerCount > 0) {
                Badge(
                  containerColor = ReactionLoveRed,
                  contentColor = Color.White
                ) {
                  Text(
                    text = "$unreadMessengerCount",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.Chat,
              contentDescription = "Encrypted Messenger",
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }
  }
}
