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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
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
import com.example.data.model.EventMeetingType
import com.example.data.model.EventRsvpStatus
import com.example.data.model.HobbyEvent
import com.example.data.model.HobbyGroup
import com.example.ui.components.OrganizerReputationBadge
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoseRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
  events: List<HobbyEvent>,
  groups: List<HobbyGroup>,
  onEventClick: (String) -> Unit,
  onUpdateRsvp: (String, EventRsvpStatus) -> Unit,
  onCreateEventClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTabIndex by remember { mutableIntStateOf(0) }
  var searchQuery by remember { mutableStateOf("") }

  val tabs = listOf("All Events", "Going / RSVP", "Live Now")

  val filteredEvents = events.filter { event ->
    val matchesTab = when (selectedTabIndex) {
      0 -> true
      1 -> event.userRsvp == EventRsvpStatus.GOING || event.userRsvp == EventRsvpStatus.MAYBE
      2 -> event.isLiveNow
      else -> true
    }
    val matchesSearch = searchQuery.isEmpty() ||
        event.title.contains(searchQuery, ignoreCase = true) ||
        event.category.contains(searchQuery, ignoreCase = true) ||
        event.groupName.contains(searchQuery, ignoreCase = true) ||
        event.locationName.contains(searchQuery, ignoreCase = true)
    matchesTab && matchesSearch
  }

  Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Top Search and Tabs
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface)
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search upcoming sessions, workshops, games...", fontSize = 14.sp) },
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
            .testTag("events_search_bar")
        )

        PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
          tabs.forEachIndexed { index, title ->
            Tab(
              selected = selectedTabIndex == index,
              onClick = { selectedTabIndex = index },
              text = {
                Text(
                  text = if (index == 2) "🔴 $title" else title,
                  fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 13.sp
                )
              },
              modifier = Modifier.testTag("events_tab_$index")
            )
          }
        }
      }

      if (filteredEvents.isEmpty()) {
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
            Text("📅", fontSize = 48.sp)
            Text(
              text = if (selectedTabIndex == 1) "You have no upcoming RSVPs" else "No scheduled events found",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Text(
              text = "Plan a local meetup, game night, or collaborative workshop.",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
              onClick = onCreateEventClick,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("empty_state_create_event_btn")
            ) {
              Text("Plan a Meetup / Workshop")
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
          items(filteredEvents, key = { it.id }) { event ->
            HobbyEventCard(
              event = event,
              onClick = { onEventClick(event.id) },
              onUpdateRsvp = { status -> onUpdateRsvp(event.id, status) }
            )
          }
          item { Spacer(modifier = Modifier.height(80.dp)) }
        }
      }
    }

    FloatingActionButton(
      onClick = onCreateEventClick,
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
        .testTag("create_event_fab")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Plan Event")
    }
  }
}

@Composable
fun HobbyEventCard(
  event: HobbyEvent,
  onClick: () -> Unit,
  onUpdateRsvp: (EventRsvpStatus) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .semantics { contentDescription = "Event card ${event.title}" }
      .testTag("event_card_${event.id}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Cover icon + Title + Live badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(if (event.isLiveNow) RoseRed.copy(alpha = 0.18f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
          ) {
            Text(event.coverIcon, fontSize = 24.sp)
          }

          Column {
            if (event.isLiveNow) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.size(8.dp).background(RoseRed, CircleShape))
                Text("LIVE BROADCAST / SESSION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RoseRed)
              }
            }
            Text(
              text = event.title,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = event.groupName,
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Meeting Type Chip
        Surface(
          color = when (event.meetingType) {
            EventMeetingType.IN_PERSON -> MaterialTheme.colorScheme.secondaryContainer
            EventMeetingType.HYBRID -> MaterialTheme.colorScheme.tertiaryContainer
            EventMeetingType.VIRTUAL_AUDIO_STAGE -> MaterialTheme.colorScheme.primaryContainer
          },
          shape = RoundedCornerShape(8.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
          ) {
            Icon(
              imageVector = when (event.meetingType) {
                EventMeetingType.IN_PERSON -> Icons.Default.LocationOn
                EventMeetingType.HYBRID -> Icons.Default.Videocam
                EventMeetingType.VIRTUAL_AUDIO_STAGE -> Icons.Default.Headphones
              },
              contentDescription = null,
              modifier = Modifier.size(12.dp)
            )
            Text(
              text = when (event.meetingType) {
                EventMeetingType.IN_PERSON -> "In-Person"
                EventMeetingType.HYBRID -> "Hybrid"
                EventMeetingType.VIRTUAL_AUDIO_STAGE -> "Audio Stage"
              },
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Date, Time & Venue
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
          Text("${event.dateFormatted} • ${event.timeFormatted}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
        Text(event.locationName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
      }

      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = event.description,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Organizer & Attendees
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        OrganizerReputationBadge(
          organizerName = event.organizerName,
          reputationScore = event.organizerReputation,
          isVerified = event.isVerifiedOrganizer,
          compact = true
        )

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Icon(Icons.Default.People, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
          Text("${event.currentRsvpCount} / ${event.maxAttendees} RSVP", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // RSVP Action Buttons Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val isGoing = event.userRsvp == EventRsvpStatus.GOING
        val isMaybe = event.userRsvp == EventRsvpStatus.MAYBE

        Button(
          onClick = {
            onUpdateRsvp(if (isGoing) EventRsvpStatus.NONE else EventRsvpStatus.GOING)
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isGoing) EmeraldGreen else MaterialTheme.colorScheme.primary
          ),
          modifier = Modifier
            .weight(1.2f)
            .testTag("rsvp_going_button_${event.id}")
        ) {
          Icon(
            imageVector = if (isGoing) Icons.Default.Check else Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(if (isGoing) "Going ✓" else "RSVP Going", fontSize = 12.sp)
        }

        OutlinedButton(
          onClick = {
            onUpdateRsvp(if (isMaybe) EventRsvpStatus.NONE else EventRsvpStatus.MAYBE)
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (isMaybe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
          ),
          modifier = Modifier
            .weight(0.8f)
            .testTag("rsvp_maybe_button_${event.id}")
        ) {
          Text(if (isMaybe) "Maybe ✓" else "Maybe", fontSize = 12.sp)
        }
      }
    }
  }
}
