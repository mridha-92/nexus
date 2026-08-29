package com.example.ui.screens.events

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventMeetingType
import com.example.data.model.EventRsvpStatus
import com.example.data.model.HobbyEvent
import com.example.ui.components.OrganizerReputationBadge
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoseRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
  event: HobbyEvent?,
  onBackClick: () -> Unit,
  onUpdateRsvp: (EventRsvpStatus) -> Unit,
  onToggleEquipment: (String) -> Unit,
  onOpenVideoLounge: () -> Unit,
  onEndorseOrganizer: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (event == null) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Event not found")
    }
    return
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(event.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
        navigationIcon = {
          IconButton(onClick = onBackClick, modifier = Modifier.testTag("event_detail_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          if (event.meetingType != EventMeetingType.IN_PERSON) {
            IconButton(onClick = onOpenVideoLounge, modifier = Modifier.testTag("event_audio_stage_action")) {
              Icon(Icons.Default.Videocam, contentDescription = "Join Audio/Video Stage")
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
      // Event Header Card
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
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (event.isLiveNow) RoseRed.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Text(event.coverIcon, fontSize = 32.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
              if (event.isLiveNow) {
                Text("● LIVE BROADCAST IN PROGRESS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RoseRed)
              }
              Text(event.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
              Text(event.groupName, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Date & Time Banner
          Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Column {
                Text(event.dateFormatted, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(event.timeFormatted, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Venue Location
          Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldGreen)
              Column {
                Text(event.locationName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Coordinates: ${event.latitude}, ${event.longitude}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = event.description,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp
          )
        }
      }

      // Organizer Reputation
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Verified Event Host", fontSize = 14.sp, fontWeight = FontWeight.Bold)
          OrganizerReputationBadge(
            organizerName = event.organizerName,
            reputationScore = event.organizerReputation,
            isVerified = event.isVerifiedOrganizer,
            compact = false,
            onEndorseClick = onEndorseOrganizer
          )
        }
      }

      // Collaborative Equipment & Checklist
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.Inventory, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Text("Equipment & Supply Checklist", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Text("${event.equipmentList.count { it.isChecked }}/${event.equipmentList.size} Ready", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }

          Text(
            text = "Tap any item to volunteer bringing equipment for this meetup. Stored offline.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          event.equipmentList.forEach { item ->
            Surface(
              color = if (item.isChecked) EmeraldGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onToggleEquipment(item.id) }
                .testTag("equipment_item_${item.id}")
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(
                    imageVector = if (item.isChecked) Icons.Default.CheckCircle else Icons.Default.CheckBoxOutlineBlank,
                    contentDescription = null,
                    tint = if (item.isChecked) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                  )
                  Column {
                    Text(
                      text = item.name,
                      fontSize = 13.sp,
                      fontWeight = if (item.isChecked) FontWeight.Bold else FontWeight.Normal
                    )
                    if (item.assignedTo.isNotBlank()) {
                      Text("Provided by: ${item.assignedTo}", fontSize = 11.sp, color = if (item.isChecked) EmeraldGreen else MaterialTheme.colorScheme.primary)
                    }
                  }
                }

                Surface(
                  color = if (item.isChecked) EmeraldGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text(
                    text = if (item.isChecked) "Confirmed" else "Needs Volunteer",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isChecked) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }
        }
      }

      // RSVP Action Bar
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Your Attendance RSVP", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("${event.currentRsvpCount} of ${event.maxAttendees} spots filled", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val isGoing = event.userRsvp == EventRsvpStatus.GOING
            val isMaybe = event.userRsvp == EventRsvpStatus.MAYBE
            val isNone = event.userRsvp == EventRsvpStatus.NONE

            Button(
              onClick = { onUpdateRsvp(EventRsvpStatus.GOING) },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isGoing) EmeraldGreen else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isGoing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
              ),
              modifier = Modifier.weight(1f).testTag("event_detail_rsvp_going")
            ) {
              Text(if (isGoing) "Going ✓" else "Going")
            }

            Button(
              onClick = { onUpdateRsvp(EventRsvpStatus.MAYBE) },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isMaybe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isMaybe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
              ),
              modifier = Modifier.weight(1f).testTag("event_detail_rsvp_maybe")
            ) {
              Text(if (isMaybe) "Maybe ✓" else "Maybe")
            }

            Button(
              onClick = { onUpdateRsvp(EventRsvpStatus.NONE) },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isNone) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isNone) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
              ),
              modifier = Modifier.weight(1f).testTag("event_detail_rsvp_not_going")
            ) {
              Text("Can't Go")
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}
