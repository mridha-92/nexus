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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CollabDocument
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentEditorScreen(
  document: CollabDocument?,
  onBackClick: () -> Unit,
  onSaveContent: (String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  if (document == null) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Document not found")
    }
    return
  }

  var contentText by remember(document.id) { mutableStateOf(document.content) }
  var isAutoSaved by remember { mutableStateOf(true) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(document.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Box(modifier = Modifier.size(6.dp).background(EmeraldGreen, CircleShape))
              Text("Real-Time Co-Editing • ${document.groupName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = onBackClick, modifier = Modifier.testTag("doc_editor_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          // Active peer presence bubbles
          Row(
            modifier = Modifier.padding(end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy((-6).dp)
          ) {
            listOf("🎨", "🤖", "🌿").forEach { emoji ->
              Box(
                modifier = Modifier
                  .size(26.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
              ) {
                Text(emoji, fontSize = 12.sp)
              }
            }
          }
          IconButton(
            onClick = {
              onSaveContent(document.id, contentText)
              isAutoSaved = true
            },
            modifier = Modifier.testTag("doc_editor_save_btn")
          ) {
            Icon(Icons.Default.Save, contentDescription = "Save")
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
        .background(MaterialTheme.colorScheme.background)
    ) {
      // Toolbar with Markdown shortcuts & Auto-save status
      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.surface,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable {
                  contentText = "$contentText\n\n### Heading"
                  onSaveContent(document.id, contentText)
                }
            ) {
              Text("H1", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.surface,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable {
                  contentText = "$contentText **bold text**"
                  onSaveContent(document.id, contentText)
                }
            ) {
              Text("B", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.surface,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable {
                  contentText = "$contentText\n- [ ] Task item"
                  onSaveContent(document.id, contentText)
                }
            ) {
              Text("☑ Task", fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.surface,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable {
                  contentText = "$contentText\n```kotlin\n// Code snippet\n```"
                  onSaveContent(document.id, contentText)
                }
            ) {
              Text("</>", fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(Icons.Default.CloudDone, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
            Text("Saved v${document.version}", fontSize = 11.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
          }
        }
      }

      // Rich text editing area
      OutlinedTextField(
        value = contentText,
        onValueChange = {
          contentText = it
          isAutoSaved = false
          onSaveContent(document.id, it)
        },
        placeholder = { Text("Start drafting collaborative guild guidelines, checklists, blueprints...") },
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = Color.Transparent,
          unfocusedBorderColor = Color.Transparent
        ),
        textStyle = androidx.compose.ui.text.TextStyle(
          fontFamily = FontFamily.Monospace,
          fontSize = 14.sp,
          lineHeight = 22.sp,
          color = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
          .testTag("collab_doc_content_field")
      )
    }
  }
}
