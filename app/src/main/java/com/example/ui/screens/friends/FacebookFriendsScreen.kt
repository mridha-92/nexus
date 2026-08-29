package com.example.ui.screens.friends

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FacebookFriend
import com.example.ui.theme.FacebookBlue
import com.example.ui.theme.SecondaryLight

@Composable
fun FacebookFriendsScreen(
  friends: List<FacebookFriend>,
  onToggleFriend: (String) -> Unit,
  onAcceptRequest: (String) -> Unit,
  onMessageClick: (FacebookFriend) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf("SUGGESTIONS") } // SUGGESTIONS, ALL_FRIENDS

  val requests = friends.filter { it.hasPendingRequest }
  val suggestions = friends.filter { !it.isFriend && !it.hasPendingRequest }
  val myFriends = friends.filter { it.isFriend }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("facebook_friends_screen"),
    contentPadding = PaddingValues(bottom = 80.dp)
  ) {
    // Header & Filter Pills
    item {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "Friends",
              style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )

            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Friends",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Filter Pills: Suggestions & Your Friends
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = if (selectedTab == "SUGGESTIONS") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clickable { selectedTab = "SUGGESTIONS" }
                .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
              Text(
                text = "Suggestions",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = if (selectedTab == "SUGGESTIONS") FacebookBlue else MaterialTheme.colorScheme.onSurface
              )
            }

            Surface(
              shape = RoundedCornerShape(20.dp),
              color = if (selectedTab == "ALL_FRIENDS") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clickable { selectedTab = "ALL_FRIENDS" }
                .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
              Text(
                text = "Your Friends (${myFriends.size})",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = if (selectedTab == "ALL_FRIENDS") FacebookBlue else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }

    if (selectedTab == "SUGGESTIONS") {
      // 1. Friend Requests Section
      if (requests.isNotEmpty()) {
        item {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 12.dp)
          ) {
            Text(
              text = "Friend Requests (${requests.size})",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "See all",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
              color = FacebookBlue
            )
          }
        }

        items(requests, key = { it.id }) { req ->
          FriendRequestItemCard(
            friend = req,
            onConfirm = { onAcceptRequest(req.id) },
            onDelete = { onToggleFriend(req.id) }
          )
        }

        item {
          HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
          )
        }
      }

      // 2. People You May Know Nearby Section
      item {
        Text(
          text = "People You May Know Nearby",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
      }

      items(suggestions, key = { it.id }) { person ->
        FriendSuggestionItemCard(
          friend = person,
          onAddFriend = { onToggleFriend(person.id) },
          onRemove = { onToggleFriend(person.id) }
        )
      }
    } else {
      // All Friends Tab
      items(myFriends, key = { it.id }) { friend ->
        MyFriendItemCard(
          friend = friend,
          onMessage = { onMessageClick(friend) }
        )
      }
    }
  }
}

@Composable
fun FriendRequestItemCard(
  friend: FacebookFriend,
  onConfirm: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 4.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(12.dp)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Text(text = friend.avatar, fontSize = 32.sp)
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = friend.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          if (friend.isVerified) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = FacebookBlue, modifier = Modifier.size(15.dp))
          }
        }

        Text(
          text = "${friend.mutualFriendsCount} mutual hobbyists • ${friend.distanceAway}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = onConfirm,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FacebookBlue),
            modifier = Modifier
              .weight(1f)
              .height(36.dp)
          ) {
            Text("Confirm", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }

          OutlinedButton(
            onClick = onDelete,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .weight(1f)
              .height(36.dp)
          ) {
            Text("Delete", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
          }
        }
      }
    }
  }
}

@Composable
fun FriendSuggestionItemCard(
  friend: FacebookFriend,
  onAddFriend: () -> Unit,
  onRemove: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 4.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(12.dp)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Text(text = friend.avatar, fontSize = 32.sp)
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = friend.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          if (friend.isVerified) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = FacebookBlue, modifier = Modifier.size(15.dp))
          }
        }

        Text(
          text = "${friend.mutualHobby} • ${friend.distanceAway}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = onAddFriend,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FacebookBlue),
            modifier = Modifier
              .weight(1f)
              .height(36.dp)
          ) {
            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Friend", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }

          OutlinedButton(
            onClick = onRemove,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .weight(1f)
              .height(36.dp)
          ) {
            Text("Remove", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      }
    }
  }
}

@Composable
fun MyFriendItemCard(
  friend: FacebookFriend,
  onMessage: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 4.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(12.dp)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(50.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Text(text = friend.avatar, fontSize = 24.sp)
        if (friend.isOnline) {
          Box(
            modifier = Modifier
              .size(12.dp)
              .clip(CircleShape)
              .background(SecondaryLight)
              .align(Alignment.BottomEnd)
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = friend.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          if (friend.isVerified) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = FacebookBlue, modifier = Modifier.size(14.dp))
          }
        }
        Text(
          text = if (friend.isOnline) "Active Now • ${friend.mutualHobby}" else friend.mutualHobby,
          style = MaterialTheme.typography.bodySmall,
          color = if (friend.isOnline) SecondaryLight else MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
          .clickable { onMessage() }
          .padding(horizontal = 12.dp, vertical = 8.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.AutoMirrored.Outlined.Chat,
            contentDescription = "Message",
            tint = FacebookBlue,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Message",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = FacebookBlue
          )
        }
      }
    }
  }
}
