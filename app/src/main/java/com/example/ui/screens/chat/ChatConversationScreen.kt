package com.example.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMediaType
import com.example.data.model.ChatMessage
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoseRed
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.TertiaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatConversationScreen(
  conversationId: String,
  title: String,
  messages: List<ChatMessage>,
  onBackClick: () -> Unit,
  onSendMessage: (text: String, mediaType: ChatMediaType, caption: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var inputText by remember { mutableStateOf("") }
  var showMediaPicker by remember { mutableStateOf(false) }
  val listState = rememberLazyListState()

  val convMessages = messages.filter { it.conversationId == conversationId }

  LaunchedEffect(convMessages.size) {
    if (convMessages.isNotEmpty()) {
      listState.animateScrollToItem(convMessages.size - 1)
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
              Icon(Icons.Default.Lock, contentDescription = "E2EE", tint = EmeraldGreen, modifier = Modifier.size(14.dp))
            }
            Text("AES-256 End-to-End Encrypted • SHA256:7f3a:89d2", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        },
        navigationIcon = {
          IconButton(onClick = onBackClick, modifier = Modifier.testTag("chat_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
      // Security Verification Bar
      Surface(
        color = EmeraldGreen.copy(alpha = 0.08f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.Shield, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
          Text(
            text = "Direct messages are sealed with mutual public keys. Offline sync queue enabled.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Messages List
      LazyColumn(
        state = listState,
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(convMessages, key = { it.id }) { msg ->
          ChatMessageBubble(msg = msg)
        }
      }

      // Media Options Sheet (Collapsible)
      AnimatedVisibility(visible = showMediaPicker) {
        Surface(
          color = MaterialTheme.colorScheme.surfaceVariant,
          shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            MediaOptionItem(
              icon = Icons.Default.PhotoCamera,
              label = "Photo / Media",
              onClick = {
                onSendMessage("Shared photo attachment", ChatMediaType.IMAGE, "hobby_preview.jpg (Encrypted AES)")
                showMediaPicker = false
              }
            )
            MediaOptionItem(
              icon = Icons.Default.Mic,
              label = "Voice Note",
              onClick = {
                onSendMessage("Voice note (0:24s)", ChatMediaType.AUDIO_NOTE, "Audio clip 24s")
                showMediaPicker = false
              }
            )
            MediaOptionItem(
              icon = Icons.Default.Description,
              label = "Hobby Guide / Doc",
              onClick = {
                onSendMessage("Attached Workshop Checklist & PDF", ChatMediaType.DOCUMENT, "Guide_v1.pdf")
                showMediaPicker = false
              }
            )
            MediaOptionItem(
              icon = Icons.Default.LocationOn,
              label = "Venue Pin",
              onClick = {
                onSendMessage("Meetup Location Pin: Arts District", ChatMediaType.LOCATION_PIN, "GPS 37.7749, -122.4194")
                showMediaPicker = false
              }
            )
          }
        }
      }

      // Bottom Chat Input Bar
      Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          IconButton(
            onClick = { showMediaPicker = !showMediaPicker },
            modifier = Modifier
              .size(40.dp)
              .testTag("chat_attach_media_button")
          ) {
            Icon(Icons.Default.AttachFile, contentDescription = "Attach Media or Document", tint = MaterialTheme.colorScheme.primary)
          }

          OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            placeholder = { Text("Write encrypted message...", fontSize = 14.sp) },
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("chat_input_text")
          )

          IconButton(
            onClick = {
              if (inputText.isNotBlank()) {
                onSendMessage(inputText, ChatMediaType.NONE, "")
                inputText = ""
              }
            },
            modifier = Modifier
              .size(42.dp)
              .background(MaterialTheme.colorScheme.primary, CircleShape)
              .testTag("chat_send_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Send,
              contentDescription = "Send",
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun ChatMessageBubble(msg: ChatMessage) {
  val isMe = msg.isFromMe

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .semantics { contentDescription = "Message from ${msg.senderName}: ${msg.text}" }
      .testTag("chat_message_${msg.id}"),
    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
    verticalAlignment = Alignment.Bottom
  ) {
    if (!isMe) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Text(msg.senderAvatar, fontSize = 16.sp)
      }
      Spacer(modifier = Modifier.width(8.dp))
    }

    Column(
      horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
      modifier = Modifier.widthIn(max = 280.dp)
    ) {
      if (!isMe) {
        Text(
          text = msg.senderName,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
        )
      }

      Surface(
        color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(
          topStart = 16.dp,
          topEnd = 16.dp,
          bottomStart = if (isMe) 16.dp else 4.dp,
          bottomEnd = if (isMe) 4.dp else 16.dp
        ),
        shadowElevation = 1.dp
      ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
          // Media attachments display
          when (msg.mediaType) {
            ChatMediaType.IMAGE -> {
              Surface(
                color = Color.Black.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 6.dp)
              ) {
                Row(
                  modifier = Modifier.padding(8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(Icons.Default.Image, contentDescription = null, tint = if (isMe) Color.White else MaterialTheme.colorScheme.primary)
                  Text(
                    text = msg.mediaCaption.ifEmpty { "Encrypted Photo Attachment" },
                    fontSize = 12.sp,
                    color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
            ChatMediaType.AUDIO_NOTE -> {
              Surface(
                color = Color.Black.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 6.dp)
              ) {
                Row(
                  modifier = Modifier.padding(8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(Icons.Default.Mic, contentDescription = null, tint = if (isMe) Color.White else MaterialTheme.colorScheme.primary)
                  Text("▶ ılılılı 0:24 Voice Note", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface)
                }
              }
            }
            ChatMediaType.DOCUMENT -> {
              Surface(
                color = Color.Black.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 6.dp)
              ) {
                Row(
                  modifier = Modifier.padding(8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(Icons.Default.Description, contentDescription = null, tint = if (isMe) Color.White else MaterialTheme.colorScheme.secondary)
                  Text(msg.mediaCaption.ifEmpty { "Hobby Document" }, fontSize = 12.sp, color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface)
                }
              }
            }
            ChatMediaType.LOCATION_PIN -> {
              Surface(
                color = Color.Black.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 6.dp)
              ) {
                Row(
                  modifier = Modifier.padding(8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(Icons.Default.LocationOn, contentDescription = null, tint = RoseRed)
                  Text(msg.mediaCaption.ifEmpty { "Venue Location Pin" }, fontSize = 12.sp, color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface)
                }
              }
            }
            ChatMediaType.NONE -> {}
          }

          Text(
            text = msg.text,
            fontSize = 14.sp,
            color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
          )
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
      ) {
        Text(
          text = msg.timestampFormatted,
          fontSize = 10.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (isMe) {
          Icon(
            imageVector = if (msg.syncStatus == "SYNCED") Icons.Default.CloudDone else Icons.Default.CloudOff,
            contentDescription = "Sync status ${msg.syncStatus}",
            tint = if (msg.syncStatus == "SYNCED") EmeraldGreen else TertiaryDark,
            modifier = Modifier.size(12.dp)
          )
        }
      }
    }
  }
}

@Composable
fun MediaOptionItem(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(4.dp),
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable { onClick() }
      .padding(8.dp)
  ) {
    Box(
      modifier = Modifier
        .size(44.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surface),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
    }
    Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium)
  }
}
