package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.model.SyncState
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TertiaryDark

@Composable
fun SyncStatusBanner(
  syncState: SyncState,
  onSyncClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bgColor = when (syncState) {
    SyncState.ONLINE_SYNCED -> EmeraldGreen.copy(alpha = 0.12f)
    SyncState.OFFLINE_CACHED -> TertiaryDark.copy(alpha = 0.18f)
    SyncState.SYNCING_IN_PROGRESS -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
  }

  val textColor = when (syncState) {
    SyncState.ONLINE_SYNCED -> EmeraldGreen
    SyncState.OFFLINE_CACHED -> TertiaryDark
    SyncState.SYNCING_IN_PROGRESS -> MaterialTheme.colorScheme.primary
  }

  val label = when (syncState) {
    SyncState.ONLINE_SYNCED -> "Encrypted Cloud Sync Active • AES-256 E2EE"
    SyncState.OFFLINE_CACHED -> "Offline Mode Active • Local Room Cache"
    SyncState.SYNCING_IN_PROGRESS -> "Synchronizing changes across devices..."
  }

  Surface(
    color = bgColor,
    shape = RoundedCornerShape(0.dp),
    modifier = modifier
      .fillMaxWidth()
      .clickable { onSyncClick() }
      .semantics { contentDescription = label }
      .testTag("sync_status_banner")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        when (syncState) {
          SyncState.ONLINE_SYNCED -> {
            Icon(
              imageVector = Icons.Default.CloudDone,
              contentDescription = "Cloud Connected",
              tint = textColor,
              modifier = Modifier.size(16.dp)
            )
          }
          SyncState.OFFLINE_CACHED -> {
            Icon(
              imageVector = Icons.Default.CloudOff,
              contentDescription = "Offline Cache",
              tint = textColor,
              modifier = Modifier.size(16.dp)
            )
          }
          SyncState.SYNCING_IN_PROGRESS -> {
            CircularProgressIndicator(
              modifier = Modifier.size(14.dp),
              strokeWidth = 2.dp,
              color = textColor
            )
          }
        }

        Text(
          text = label,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = textColor
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = "End-to-end encrypted",
          tint = textColor.copy(alpha = 0.8f),
          modifier = Modifier.size(12.dp)
        )
        Text(
          text = if (syncState == SyncState.SYNCING_IN_PROGRESS) "Syncing" else "Sync Now",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = textColor
        )
      }
    }
  }
}
