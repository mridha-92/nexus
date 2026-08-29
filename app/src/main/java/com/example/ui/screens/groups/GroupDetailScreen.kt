package com.example.ui.screens.groups

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CollabDocument
import com.example.data.model.EventRsvpStatus
import com.example.data.model.HobbyEvent
import com.example.data.model.HobbyGroup
import com.example.ui.components.OrganizerReputationBadge
import com.example.ui.theme.EmeraldGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GroupDetailScreen(
  group: HobbyGroup?,
  events: List<HobbyEvent>,
  documents: List<CollabDocument>,
  onBackClick: () -> Unit,
  onToggleMembership: (String, Boolean) -> Unit,
  onOpenChat: (String) -> Unit,
  onOpenDoc: (String) -> Unit,
  onOpenEvent: (String) -> Unit,
  onOpenVideoLounge: () -> Unit,
  onEndorseOrganizer: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (group == null) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Guild not found")
    }
    return
  }

  val groupEvents = events.filter { it.groupId == group.id }
  val groupDocs = documents.filter { it.groupId == group.id }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(group.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
        navigationIcon = {
          IconButton(onClick = onBackClick, modifier = Modifier.testTag("group_detail_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(
            onClick = { onOpenChat(group.id) },
            modifier = Modifier.testTag("group_detail_chat_action")
          ) {
            Icon(Icons.Default.Chat, contentDescription = "Encrypted Group Chat")
          }
          IconButton(
            onClick = onOpenVideoLounge,
            modifier = Modifier.testTag("group_detail_video_action")
          ) {
            Icon(Icons.Default.Videocam, contentDescription = "Virtual Stage / Video Lounge")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { paddingValues ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Hero Guild Header Card
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(group.coverColorHex).copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Text(group.iconEmoji, fontSize = 34.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = group.name,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }

              Spacer(modifier = Modifier.height(4.dp))
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Surface(
                  color = if (group.isPrivate) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text(
                    text = if (group.isPrivate) "Private Circle" else "Open Guild",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (group.isPrivate) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                  )
                }

                Text(
                  text = "${group.membersCount} Members",
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = group.description,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp
          )

          Spacer(modifier = Modifier.height(12.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Text(group.locationName, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Membership Button
          Button(
            onClick = { onToggleMembership(group.id, group.isMember) },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (group.isMember) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary,
              contentColor = if (group.isMember) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("group_detail_membership_button")
          ) {
            Icon(
              imageVector = if (group.isMember) Icons.Default.Check else Icons.Default.PersonAdd,
              contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (group.isMember) "You are a Member (${group.membershipRole})" else if (group.isPrivate) "Request Private Membership" else "Join Interest Guild",
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Organizer Reputation Verification Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Organizer Reliability & Trust",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          OrganizerReputationBadge(
            organizerName = group.organizerName,
            reputationScore = group.organizerReputation,
            isVerified = group.isVerifiedOrganizer,
            compact = false,
            onEndorseClick = onEndorseOrganizer
          )
        }
      }

      // Community Rules Card
      if (group.rules.isNotEmpty()) {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.Gavel, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
              Text("Community Standards & Rules", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            group.rules.forEachIndexed { idx, rule ->
              Text(
                text = "${idx + 1}. $rule",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Upcoming Guild Events
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Upcoming Guild Events", fontSize = 16.sp, fontWeight = FontWeight.Bold)
          Text("${groupEvents.size} Planned", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        if (groupEvents.isEmpty()) {
          Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
          ) {
            Text(
              text = "No events scheduled yet. Start an event proposal!",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(16.dp)
            )
          }
        } else {
          groupEvents.forEach { event ->
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("group_event_item_${event.id}")
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Text(event.coverIcon, fontSize = 24.sp)
                  Column {
                    Text(event.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                    Text("${event.dateFormatted} • ${event.timeFormatted}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                  }
                }

                OutlinedButton(
                  onClick = { onOpenEvent(event.id) },
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Text("Details", fontSize = 12.sp)
                }
              }
            }
          }
        }
      }

      // Collaborative Documents for this guild
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Collaborative Guild Docs", fontSize = 16.sp, fontWeight = FontWeight.Bold)
          Text("${groupDocs.size} Documents", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        groupDocs.forEach { doc ->
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("group_doc_item_${doc.id}")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
              ) {
                Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Column {
                  Text(doc.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                  Text("Editors: ${doc.activeEditors.joinToString(", ")}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }

              OutlinedButton(
                onClick = { onOpenDoc(doc.id) },
                shape = RoundedCornerShape(10.dp)
              ) {
                Text("Open Doc", fontSize = 12.sp)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}
