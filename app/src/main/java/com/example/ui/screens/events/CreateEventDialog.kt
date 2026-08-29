package com.example.ui.screens.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EquipmentItem
import com.example.data.model.EventMeetingType
import com.example.data.model.HobbyGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventDialog(
  groups: List<HobbyGroup>,
  onDismiss: () -> Unit,
  onCreate: (groupId: String, title: String, cat: String, desc: String, date: String, time: String, loc: String, max: Int, type: EventMeetingType, eq: List<EquipmentItem>) -> Unit
) {
  var selectedGroup by remember { mutableStateOf(groups.firstOrNull() ?: HobbyGroup("g_1", "General", "General", "", false, 1, "", "", 95, true, "", 0.0, 0.0, "🎯", 0xFF4F46E5, emptyList(), emptyList())) }
  var expandedGroupDropdown by remember { mutableStateOf(false) }

  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var dateFormatted by remember { mutableStateOf("This Sunday") }
  var timeFormatted by remember { mutableStateOf("3:00 PM - 6:00 PM") }
  var locationName by remember { mutableStateOf("Makerspace Workshop Lounge") }
  var maxAttendees by remember { mutableIntStateOf(16) }
  var meetingType by remember { mutableStateOf(EventMeetingType.IN_PERSON) }
  var equipmentRaw by remember { mutableStateOf("Main Table Board:Organizer:true,Spare Game Mats::false,Score Trackers::false") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Plan Hobbyist Meetup / Event", fontWeight = FontWeight.Bold) },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Group Selector Dropdown
        ExposedDropdownMenuBox(
          expanded = expandedGroupDropdown,
          onExpandedChange = { expandedGroupDropdown = !expandedGroupDropdown }
        ) {
          OutlinedTextField(
            value = "${selectedGroup.iconEmoji} ${selectedGroup.name}",
            onValueChange = {},
            readOnly = true,
            label = { Text("Host Guild") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGroupDropdown) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
              .testTag("event_group_selector")
          )
          ExposedDropdownMenu(
            expanded = expandedGroupDropdown,
            onDismissRequest = { expandedGroupDropdown = false }
          ) {
            groups.forEach { group ->
              DropdownMenuItem(
                text = { Text("${group.iconEmoji} ${group.name}") },
                onClick = {
                  selectedGroup = group
                  expandedGroupDropdown = false
                }
              )
            }
          }
        }

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Event Title") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("create_event_title_input")
        )

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Event Description & Agenda") },
          maxLines = 3,
          modifier = Modifier.fillMaxWidth().testTag("create_event_desc_input")
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = dateFormatted,
            onValueChange = { dateFormatted = it },
            label = { Text("Date") },
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = timeFormatted,
            onValueChange = { timeFormatted = it },
            label = { Text("Time") },
            modifier = Modifier.weight(1f)
          )
        }

        OutlinedTextField(
          value = locationName,
          onValueChange = { locationName = it },
          label = { Text("Venue / Location Name") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        // Meeting Type Chips
        Text("Format:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          EventMeetingType.values().forEach { type ->
            FilterChip(
              selected = meetingType == type,
              onClick = { meetingType = type },
              label = {
                Text(
                  when (type) {
                    EventMeetingType.IN_PERSON -> "In-Person"
                    EventMeetingType.HYBRID -> "Hybrid"
                    EventMeetingType.VIRTUAL_AUDIO_STAGE -> "Audio Stage"
                  },
                  fontSize = 11.sp
                )
              }
            )
          }
        }

        OutlinedTextField(
          value = equipmentRaw,
          onValueChange = { equipmentRaw = it },
          label = { Text("Equipment Checklist (Item:Assigned:Checked)") },
          supportingText = { Text("Comma-separated items", fontSize = 10.sp) },
          maxLines = 2,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            val items = equipmentRaw.split(",").mapIndexedNotNull { idx, raw ->
              val parts = raw.split(":")
              if (parts.isNotEmpty()) {
                EquipmentItem(
                  id = "eq_new_$idx",
                  name = parts.getOrNull(0) ?: "Equipment",
                  assignedTo = parts.getOrNull(1) ?: "",
                  isChecked = parts.getOrNull(2)?.toBoolean() ?: false
                )
              } else null
            }
            onCreate(
              selectedGroup.id,
              title,
              selectedGroup.category,
              description,
              dateFormatted,
              timeFormatted,
              locationName,
              maxAttendees,
              meetingType,
              items
            )
          }
        },
        enabled = title.isNotBlank(),
        modifier = Modifier.testTag("confirm_create_event_button")
      ) {
        Text("Schedule Event")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
