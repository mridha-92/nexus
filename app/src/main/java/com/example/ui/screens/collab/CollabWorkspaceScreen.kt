package com.example.ui.screens.collab

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.model.CollabDocument
import com.example.data.model.HobbyGroup
import com.example.data.model.MilestoneItem
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoseRed
import com.example.ui.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollabWorkspaceScreen(
  documents: List<CollabDocument>,
  milestones: List<MilestoneItem>,
  groups: List<HobbyGroup>,
  onSelectDocument: (String) -> Unit,
  onToggleMilestone: (String, Boolean) -> Unit,
  onOpenVideoLounge: () -> Unit,
  onCreateDocument: (groupId: String, groupName: String, title: String, content: String) -> Unit,
  onCreateMilestone: (groupId: String, title: String, dueDate: String, member: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTabIndex by remember { mutableIntStateOf(0) }
  var searchQuery by remember { mutableStateOf("") }
  var showNewDocDialog by remember { mutableStateOf(false) }
  var showNewMilestoneDialog by remember { mutableStateOf(false) }

  val tabs = listOf("Guild Docs", "Project Milestones", "Virtual Lounge")

  Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Top Tabs
      Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
              Tab(
                selected = selectedTabIndex == index,
                onClick = { selectedTabIndex = index },
                text = { Text(title, fontSize = 13.sp, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) },
                modifier = Modifier.testTag("workspace_tab_$index")
              )
            }
          }
        }
      }

      when (selectedTabIndex) {
        0 -> {
          // Collaborative Documents Tab
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            item {
              // Real-time Collaboration Banner
              Surface(
                color = SkyBlue.copy(alpha = 0.12f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Icon(Icons.Default.Description, contentDescription = null, tint = SkyBlue)
                  Column {
                    Text("Real-Time Multi-User Collaborative Blueprints", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SkyBlue)
                    Text("Simultaneous editing with live presence, markdown sync, and zero-latency local caching.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  }
                }
              }
            }

            items(documents, key = { it.id }) { doc ->
              Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { onSelectDocument(doc.id) }
                  .testTag("workspace_doc_${doc.id}")
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                          .size(40.dp)
                          .clip(RoundedCornerShape(10.dp))
                          .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                      }
                      Column {
                        Text(doc.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(doc.groupName, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                      }
                    }

                    Surface(
                      color = MaterialTheme.colorScheme.surfaceVariant,
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Text("v${doc.version}", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))
                  Text(
                    text = doc.content.lines().take(3).joinToString(" "),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                  )

                  Spacer(modifier = Modifier.height(12.dp))

                  // Active editors presence indicators
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Box(modifier = Modifier.size(8.dp).background(EmeraldGreen, CircleShape))
                      Text("Active: ${doc.activeEditors.take(2).joinToString(", ")}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Text("Modified ${doc.lastModified}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  }
                }
              }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
          }
        }
        1 -> {
          // Milestones & Project Deadlines Tab
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            item {
              val completedCount = milestones.count { it.isCompleted }
              val total = milestones.size
              val progress = if (total > 0) completedCount.toFloat() / total else 0f

              Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text("Guild Project Deadlines & Goals", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("$completedCount / $total Done (${(progress * 100).toInt()}%)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                  }
                  LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = EmeraldGreen
                  )
                }
              }
            }

            items(milestones, key = { it.id }) { item ->
              Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { onToggleMilestone(item.id, item.isCompleted) }
                  .testTag("milestone_item_${item.id}")
              ) {
                Row(
                  modifier = Modifier.padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                  IconButton(
                    onClick = { onToggleMilestone(item.id, item.isCompleted) },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(
                      imageVector = if (item.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                      contentDescription = null,
                      tint = if (item.isCompleted) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }

                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = item.title,
                      fontWeight = FontWeight.Bold,
                      fontSize = 14.sp,
                      color = if (item.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                    Text("Due ${item.dueDate} • Assigned to ${item.assignedMember}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  }

                  Surface(
                    color = if (item.isCompleted) EmeraldGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Text(
                      text = if (item.isCompleted) "Completed" else "${item.progressPercent}%",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (item.isCompleted) EmeraldGreen else MaterialTheme.colorScheme.primary,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }
              }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
          }
        }
        2 -> {
          // Virtual Stage / Hybrid Lounge Launcher
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            Card(
              shape = RoundedCornerShape(24.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Videocam, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                }

                Text("HobbyCircle Virtual Stage & Audio Lounge", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(
                  text = "Host interactive guild workshops, live board game streams, modular synthesizer jamming, and real-time remote collaboration with integrated audio waveforms and floating reactions.",
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  lineHeight = 18.sp
                )

                Button(
                  onClick = onOpenVideoLounge,
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("launch_video_lounge_button")
                ) {
                  Icon(Icons.Default.LiveTv, contentDescription = null)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Join Virtual Community Stage", fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    }

    if (selectedTabIndex == 0) {
      FloatingActionButton(
        onClick = { showNewDocDialog = true },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(20.dp)
          .testTag("create_collab_doc_fab")
      ) {
        Icon(Icons.Default.Add, contentDescription = "New Document")
      }
    }
  }

  if (showNewDocDialog) {
    CreateDocDialog(
      groups = groups,
      onDismiss = { showNewDocDialog = false },
      onCreate = { gId, gName, title, content ->
        onCreateDocument(gId, gName, title, content)
        showNewDocDialog = false
      }
    )
  }
}

@Composable
fun CreateDocDialog(
  groups: List<HobbyGroup>,
  onDismiss: () -> Unit,
  onCreate: (groupId: String, groupName: String, title: String, content: String) -> Unit
) {
  var selectedGroup by remember { mutableStateOf(groups.firstOrNull() ?: HobbyGroup("g_1", "General", "General", "", false, 1, "", "", 95, true, "", 0.0, 0.0, "🎯", 0xFF4F46E5, emptyList(), emptyList())) }
  var title by remember { mutableStateOf("") }
  var content by remember { mutableStateOf("# Project Blueprint\n\n- [ ] Goal 1\n- [ ] Goal 2\n\nNotes:") }

  androidx.compose.material3.AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Create Collaborative Doc", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Document Title") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("new_doc_title_input")
        )
        OutlinedTextField(
          value = content,
          onValueChange = { content = it },
          label = { Text("Initial Content (Markdown)") },
          maxLines = 5,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            onCreate(selectedGroup.id, selectedGroup.name, title, content)
          }
        },
        enabled = title.isNotBlank()
      ) {
        Text("Create")
      }
    },
    dismissButton = {
      androidx.compose.material3.TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
