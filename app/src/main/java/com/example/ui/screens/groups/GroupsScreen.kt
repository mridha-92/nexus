package com.example.ui.screens.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.HobbyGroup
import com.example.ui.components.OrganizerReputationBadge

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GroupsScreen(
  groups: List<HobbyGroup>,
  onGroupClick: (String) -> Unit,
  onToggleMembership: (String, Boolean) -> Unit,
  onOpenGroupChat: (String) -> Unit,
  onOpenGroupDocs: (String) -> Unit,
  onCreateGroup: (name: String, cat: String, desc: String, isPriv: Boolean, loc: String, tags: List<String>, rules: List<String>, emoji: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTabIndex by remember { mutableIntStateOf(0) }
  var searchQuery by remember { mutableStateOf("") }
  var showCreateDialog by remember { mutableStateOf(false) }

  val tabs = listOf("All Guilds", "My Guilds", "Private Circles")

  val filteredGroups = groups.filter { group ->
    val matchesTab = when (selectedTabIndex) {
      0 -> true
      1 -> group.isMember
      2 -> group.isPrivate
      else -> true
    }
    val matchesSearch = searchQuery.isEmpty() ||
        group.name.contains(searchQuery, ignoreCase = true) ||
        group.category.contains(searchQuery, ignoreCase = true) ||
        group.tags.any { it.contains(searchQuery, ignoreCase = true) }
    matchesTab && matchesSearch
  }

  Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Top Controls
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface)
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Search
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search private interest groups, topics...", fontSize = 14.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = "Clear")
              }
            }
          },
          shape = RoundedCornerShape(16.dp),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("groups_search_bar")
        )

        // Tabs
        PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
          tabs.forEachIndexed { index, title ->
            Tab(
              selected = selectedTabIndex == index,
              onClick = { selectedTabIndex = index },
              text = {
                Text(
                  text = if (index == 1) "$title (${groups.count { it.isMember }})" else title,
                  fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 13.sp
                )
              },
              modifier = Modifier.testTag("groups_tab_$index")
            )
          }
        }
      }

      // Group Cards List
      if (filteredGroups.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Text("🔍", fontSize = 48.sp)
            Text(
              text = if (selectedTabIndex == 1) "You haven't joined any guilds yet" else "No matching interest groups found",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Explore the local directory or start your own private guild.",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
              onClick = { showCreateDialog = true },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("create_guild_empty_state_button")
            ) {
              Text("Create a New Guild")
            }
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(filteredGroups, key = { it.id }) { group ->
            HobbyGroupCard(
              group = group,
              onClick = { onGroupClick(group.id) },
              onToggleMembership = { onToggleMembership(group.id, group.isMember) },
              onOpenChat = { onOpenGroupChat(group.id) },
              onOpenDocs = { onOpenGroupDocs(group.id) }
            )
          }
          item { Spacer(modifier = Modifier.height(80.dp)) }
        }
      }
    }

    FloatingActionButton(
      onClick = { showCreateDialog = true },
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
        .testTag("create_group_fab")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Create New Guild")
    }
  }

  // --- Create Group Dialog ---
  if (showCreateDialog) {
    CreateGroupDialog(
      onDismiss = { showCreateDialog = false },
      onCreate = { name, cat, desc, isPriv, loc, tags, rules, emoji ->
        onCreateGroup(name, cat, desc, isPriv, loc, tags, rules, emoji)
        showCreateDialog = false
      }
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HobbyGroupCard(
  group: HobbyGroup,
  onClick: () -> Unit,
  onToggleMembership: () -> Unit,
  onOpenChat: () -> Unit,
  onOpenDocs: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .semantics { contentDescription = "Group card ${group.name}" }
      .testTag("group_card_${group.id}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(Color(group.coverColorHex).copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
          ) {
            Text(group.iconEmoji, fontSize = 24.sp)
          }

          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = group.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              if (group.isPrivate) {
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = "Private Circle",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(14.dp)
                )
              } else {
                Icon(
                  imageVector = Icons.Default.Public,
                  contentDescription = "Public Group",
                  tint = MaterialTheme.colorScheme.secondary,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
            Text(
              text = "${group.category} • ${group.membersCount} Members • ${group.locationName}",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = group.description,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Tags
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        group.tags.take(4).forEach { tag ->
          Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(
              text = "#$tag",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Organizer Reputation
      OrganizerReputationBadge(
        organizerName = group.organizerName,
        reputationScore = group.organizerReputation,
        isVerified = group.isVerifiedOrganizer,
        compact = true
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Actions Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          if (group.isMember) {
            IconButton(
              onClick = onOpenChat,
              modifier = Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), CircleShape)
                .testTag("group_chat_quick_button")
            ) {
              Icon(Icons.Default.Chat, contentDescription = "Group Chat", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }

            IconButton(
              onClick = onOpenDocs,
              modifier = Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f), CircleShape)
                .testTag("group_docs_quick_button")
            ) {
              Icon(Icons.Default.Description, contentDescription = "Collaborative Documents", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
            }
          }
        }

        Button(
          onClick = onToggleMembership,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (group.isMember) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
            contentColor = if (group.isMember) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
          ),
          modifier = Modifier.testTag("group_join_toggle_${group.id}")
        ) {
          Icon(
            imageVector = if (group.isMember) Icons.Default.Check else Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(if (group.isMember) "Joined" else if (group.isPrivate) "Request Join" else "Join Guild", fontSize = 12.sp)
        }
      }
    }
  }
}

@Composable
fun CreateGroupDialog(
  onDismiss: () -> Unit,
  onCreate: (name: String, cat: String, desc: String, isPriv: Boolean, loc: String, tags: List<String>, rules: List<String>, emoji: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Board Games") }
  var description by remember { mutableStateOf("") }
  var locationName by remember { mutableStateOf("Downtown Makerspace") }
  var tagsText by remember { mutableStateOf("Strategy,Weekly,Beginners Welcome") }
  var rulesText by remember { mutableStateOf("Be kind,Respect equipment,Clean up after sessions") }
  var isPrivate by remember { mutableStateOf(false) }
  var iconEmoji by remember { mutableStateOf("🎯") }

  val emojiOptions = listOf("🎯", "🎲", "🌱", "🛸", "🎛️", "🪵", "🔭", "🎨", "🧗", "📻", "☕")
  val categories = listOf("Board Games", "Urban Gardening", "Drone & Tech", "Synth & Audio", "Woodworking", "Astronomy", "Pottery & Crafts")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Create Interest Guild", fontWeight = FontWeight.Bold) },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Guild / Group Name") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("create_group_name_input")
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Icon:", fontSize = 12.sp)
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            emojiOptions.take(6).forEach { emoji ->
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(if (iconEmoji == emoji) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                  .clickable { iconEmoji = emoji },
                contentAlignment = Alignment.Center
              ) {
                Text(emoji, fontSize = 16.sp)
              }
            }
          }
        }

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Guild Description & Purpose") },
          maxLines = 3,
          modifier = Modifier.fillMaxWidth().testTag("create_group_desc_input")
        )

        OutlinedTextField(
          value = locationName,
          onValueChange = { locationName = it },
          label = { Text("Base Location / Venue") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Private Circle", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text("Requires approval to join", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          Switch(checked = isPrivate, onCheckedChange = { isPrivate = it }, modifier = Modifier.testTag("create_group_private_switch"))
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            val tags = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val rules = rulesText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            onCreate(name, category, description, isPrivate, locationName, tags, rules, iconEmoji)
          }
        },
        enabled = name.isNotBlank(),
        modifier = Modifier.testTag("confirm_create_group_button")
      ) {
        Text("Create Guild")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
