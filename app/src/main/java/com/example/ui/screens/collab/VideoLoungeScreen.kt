package com.example.ui.screens.collab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.StopScreenShare
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoseRed
import com.example.ui.theme.SkyBlue
import com.example.ui.viewmodel.VideoLoungeState

data class StageParticipant(
  val id: String,
  val name: String,
  val role: String,
  val avatarEmoji: String,
  val isSpeaking: Boolean = false,
  val isMuted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoLoungeScreen(
  state: VideoLoungeState,
  onBackClick: () -> Unit,
  onToggleMute: () -> Unit,
  onToggleCamera: () -> Unit,
  onToggleScreenShare: () -> Unit,
  onSendReaction: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val participants = listOf(
    StageParticipant("p_me", "You (Alex)", "Member", "👨‍💻", isSpeaking = !state.isMuted, isMuted = state.isMuted),
    StageParticipant("p_1", "Elena Rostova", "Host / Organizer", "🌱", isSpeaking = true, isMuted = false),
    StageParticipant("p_2", "Marcus Thorne", "Guild Moderator", "🎲", isSpeaking = false, isMuted = true),
    StageParticipant("p_3", "Dr. David Kim", "Guest Speaker", "🔭", isSpeaking = false, isMuted = false)
  )

  // Speaking pulse animation
  val infiniteTransition = rememberInfiniteTransition(label = "audio_speaking")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "audio_pulse"
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Box(modifier = Modifier.size(8.dp).background(RoseRed, CircleShape))
              Text("Live Community Lounge & Stage", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Text("Spatial HD Audio • 4 Participants Active", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        },
        navigationIcon = {
          IconButton(onClick = onBackClick, modifier = Modifier.testTag("video_lounge_back_button")) {
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
        .padding(16.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Main Participant Video Grid (2x2)
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(participants, key = { it.id }) { participant ->
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
              .height(180.dp)
              .border(
                width = if (participant.isSpeaking) 2.5.dp else 0.dp,
                color = if (participant.isSpeaking) EmeraldGreen else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
              )
              .testTag("stage_participant_${participant.id}")
          ) {
            Box(modifier = Modifier.fillMaxSize()) {
              // Center Avatar / Video Tile
              Column(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
              ) {
                Box(
                  modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                  contentAlignment = Alignment.Center
                ) {
                  Text(participant.avatarEmoji, fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = participant.name,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.onSurface,
                  maxLines = 1
                )
                Text(
                  text = participant.role,
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              // Top Right Mic status indicator
              Surface(
                color = if (participant.isMuted) RoseRed else EmeraldGreen,
                shape = CircleShape,
                modifier = Modifier
                  .align(Alignment.TopEnd)
                  .padding(8.dp)
                  .size(24.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = if (participant.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                  )
                }
              }
            }
          }
        }
      }

      // Floating Reactions Bar
      if (state.activeReactions.isNotEmpty()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
          horizontalArrangement = Arrangement.Center
        ) {
          state.activeReactions.takeLast(4).forEach { emoji ->
            Text(emoji, fontSize = 28.sp, modifier = Modifier.padding(horizontal = 4.dp))
          }
        }
      }

      // Reaction Picker Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        listOf("👏", "🔥", "🚀", "❤️", "🎯", "🎉").forEach { emoji ->
          Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = CircleShape,
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .clickable { onSendReaction(emoji) }
              .testTag("reaction_button_$emoji")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(emoji, fontSize = 18.sp)
            }
          }
        }
      }

      // Bottom Control Bar
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Mute Button
          IconButton(
            onClick = onToggleMute,
            modifier = Modifier
              .size(48.dp)
              .background(if (state.isMuted) RoseRed.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant, CircleShape)
              .testTag("stage_toggle_mute")
          ) {
            Icon(
              imageVector = if (state.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
              contentDescription = "Toggle Mute",
              tint = if (state.isMuted) RoseRed else MaterialTheme.colorScheme.onSurface
            )
          }

          // Camera Button
          IconButton(
            onClick = onToggleCamera,
            modifier = Modifier
              .size(48.dp)
              .background(if (!state.isCameraOn) RoseRed.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant, CircleShape)
              .testTag("stage_toggle_camera")
          ) {
            Icon(
              imageVector = if (state.isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
              contentDescription = "Toggle Camera",
              tint = if (!state.isCameraOn) RoseRed else MaterialTheme.colorScheme.onSurface
            )
          }

          // Screen Share Button
          IconButton(
            onClick = onToggleScreenShare,
            modifier = Modifier
              .size(48.dp)
              .background(if (state.isScreenSharing) EmeraldGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant, CircleShape)
              .testTag("stage_toggle_screenshare")
          ) {
            Icon(
              imageVector = if (state.isScreenSharing) Icons.Default.ScreenShare else Icons.Default.StopScreenShare,
              contentDescription = "Toggle Screen Share",
              tint = if (state.isScreenSharing) EmeraldGreen else MaterialTheme.colorScheme.onSurface
            )
          }

          // Leave Call Button
          IconButton(
            onClick = onBackClick,
            modifier = Modifier
              .size(48.dp)
              .background(RoseRed, CircleShape)
              .testTag("stage_leave_call")
          ) {
            Icon(Icons.Default.CallEnd, contentDescription = "Leave Stage", tint = Color.White)
          }
        }
      }
    }
  }
}
