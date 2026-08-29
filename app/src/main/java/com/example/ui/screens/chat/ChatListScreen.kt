package com.example.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.HobbyGroup
import com.example.ui.theme.EmeraldGreen

data class ConversationItem(
  val id: String,
  val title: String,
  val subtitle: String,
  val avatar: String,
  val isGroup: Boolean,
  val isEncrypted: Boolean = true,
  val lastMessage: String,
  val lastTime: String,
  val unreadCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
  groups: List<HobbyGroup>,
  messages: List<ChatMessage>,
  onSelectConversation: (String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTabIndex by remember { mutableIntStateOf(0) }
  var searchQuery by remember { mutableStateOf("") }

  // Build conversation list from groups and direct peers
  val conversations = remember(groups, messages) {
    val list = mutableListOf<ConversationItem>()

    // Guild channels
    groups.filter { it.isMember }.forEach { group ->
      val lastMsg = messages.filter { it.conversationId == group.id }.lastOrNull()
      list.add(
        ConversationItem(
          id = group.id,
          title = group.name,
          subtitle = "${group.membersCount} members • Guild Channel",
          avatar = group.iconEmoji,
          isGroup = true,
          isEncrypted = true,
          lastMessage = lastMsg?.text ?: "Welcome to ${group.name} encrypted channel",
          lastTime = lastMsg?.timestampFormatted ?: "Active",
          unreadCount = 0
        )
      )
    }

    // Direct Messages
    val dmPeers = listOf(
      ConversationItem(
        id = "dm_elena",
        title = "Elena Rostova (Organizer)",
        subtitle = "GreenThumb Urban Gardeners • Verified Host",
        avatar = "🌱",
        isGroup = false,
        isEncrypted = true,
        lastMessage = messages.filter { it.conversationId == "dm_elena" }.lastOrNull()?.text ?: "All set Elena! Wireless lavalier mics are configured.",
        lastTime = "Yesterday",
        unreadCount = 1
      ),
      ConversationItem(
        id = "dm_marcus",
        title = "Marcus Thorne",
        subtitle = "Metropolis Board Game Guild",
        avatar = "🎲",
        isGroup = false,
        isEncrypted = true,
        lastMessage = "Let me know if you want to test the new Dune expansion!",
        lastTime = "2d ago",
        unreadCount = 0
      )
    )
    list.addAll(dmPeers)
    list
  }

  val filtered = conversations.filter {
    val matchesTab = when (selectedTabIndex) {
      0 -> true
      1 -> it.isGroup
      2 -> !it.isGroup
      else -> true
    }
    val matchesSearch = searchQuery.isEmpty() || it.title.contains(searchQuery, ignoreCase = true)
    matchesTab && matchesSearch
  }

  Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Header Banner
      Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          // Encryption Safety Banner
          Surface(
            color = EmeraldGreen.copy(alpha = 0.12f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.Shield, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
              Column {
                Text(
                  text = "End-to-End Encrypted Messaging (AES-256)",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = EmeraldGreen
                )
                Text(
                  text = "Messages & media attachments are encrypted on device and synced securely offline.",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          // Search Bar
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search conversations & encrypted DMs...", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("chats_search_input")
          )

          // Tabs
          PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
            listOf("All Chats", "Guild Channels", "Direct Messages").forEachIndexed { idx, tabTitle ->
              Tab(
                selected = selectedTabIndex == idx,
                onClick = { selectedTabIndex = idx },
                text = { Text(tabTitle, fontSize = 13.sp, fontWeight = if (selectedTabIndex == idx) FontWeight.Bold else FontWeight.Normal) },
                modifier = Modifier.testTag("chats_tab_$idx")
              )
            }
          }
        }
      }

      // Conversations List
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(filtered, key = { it.id }) { conv ->
          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onSelectConversation(conv.id, conv.title) }
              .semantics { contentDescription = "Conversation with ${conv.title}" }
              .testTag("conversation_item_${conv.id}")
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(50.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
              ) {
                Text(conv.avatar, fontSize = 24.sp)
              }

              Column(modifier = Modifier.weight(1f)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Text(
                      text = conv.title,
                      fontWeight = FontWeight.Bold,
                      fontSize = 15.sp,
                      color = MaterialTheme.colorScheme.onSurface,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                      imageVector = Icons.Default.Lock,
                      contentDescription = "Encrypted",
                      tint = EmeraldGreen,
                      modifier = Modifier.size(12.dp)
                    )
                  }

                  Text(
                    text = conv.lastTime,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Spacer(modifier = Modifier.height(3.dp))
                Text(
                  text = conv.lastMessage,
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              if (conv.unreadCount > 0) {
                Box(
                  modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "${conv.unreadCount}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                  )
                }
              }
            }
          }
        }
        item { Spacer(modifier = Modifier.height(40.dp)) }
      }
    }
  }
}
