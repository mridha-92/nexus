package com.example.ui.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SyncState
import com.example.data.model.UserProfile
import com.example.ui.theme.FacebookBlue
import com.example.ui.theme.SecondaryLight

data class MenuShortcutItem(
  val id: String,
  val title: String,
  val icon: ImageVector,
  val iconColor: Color,
  val badgeText: String? = null
)

@Composable
fun FacebookMenuScreen(
  userProfile: UserProfile,
  syncState: SyncState,
  onNavigateToProfile: () -> Unit,
  onNavigateToRadar: () -> Unit,
  onNavigateToGroups: () -> Unit,
  onNavigateToEvents: () -> Unit,
  onNavigateToCollab: () -> Unit,
  onNavigateToLiveStage: () -> Unit,
  onNavigateToMessenger: () -> Unit,
  onNavigateToNotifications: () -> Unit,
  onToggleDarkMode: (String) -> Unit,
  onToggleOfflineMode: () -> Unit,
  onTriggerSync: () -> Unit,
  onOpenOnboarding: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shortcuts = listOf(
    MenuShortcutItem("radar", "Radar Discovery", Icons.Outlined.Sensors, FacebookBlue, "Live"),
    MenuShortcutItem("groups", "Guilds & Groups", Icons.Outlined.Groups, Color(0xFF10B981), "6 Active"),
    MenuShortcutItem("events", "Events & Meetups", Icons.Outlined.Event, Color(0xFFF59E0B), "5 Planned"),
    MenuShortcutItem("collab", "Collab Workspace", Icons.Outlined.Description, Color(0xFF8B5CF6), "Blueprints"),
    MenuShortcutItem("live", "Live Video Lounge", Icons.Outlined.Mic, Color(0xFFFA383E), "On-Air"),
    MenuShortcutItem("messenger", "Encrypted Messenger", Icons.AutoMirrored.Outlined.Chat, FacebookBlue, "E2EE"),
    MenuShortcutItem("saved", "Saved Posts & Blueprints", Icons.Outlined.BookmarkBorder, Color(0xFF0284C7)),
    MenuShortcutItem("onboarding", "Community Guide", Icons.Default.HelpOutline, Color(0xFF6366F1))
  )

  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("facebook_menu_screen"),
    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 90.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 1. Header & Search
    item(span = { GridItemSpan(2) }) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = "Menu",
          style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .clickable { /* search */ }
          ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Menu", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
          }
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .clickable { onNavigateToProfile() }
          ) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
          }
        }
      }
    }

    // 2. User Profile Card
    item(span = { GridItemSpan(2) }) {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToProfile() }
          .testTag("menu_profile_card")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(14.dp)
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(52.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer)
          ) {
            Text(text = userProfile.avatarEmoji, fontSize = 28.sp)
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = userProfile.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              if (userProfile.isVerifiedOrganizer) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = FacebookBlue,
                  modifier = Modifier.size(16.dp)
                )
              }
            }

            Text(
              text = "Reputation ${userProfile.reputationScore}% • ${userProfile.location}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
              text = "View your profile",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
              color = FacebookBlue
            )
          }
        }
      }
    }

    // 3. Shortcuts Section Title
    item(span = { GridItemSpan(2) }) {
      Text(
        text = "All Shortcuts",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 8.dp)
      )
    }

    // 4. Grid Shortcut Tiles (Facebook Style)
    items(shortcuts.size) { index ->
      val item = shortcuts[index]
      MenuShortcutCard(
        item = item,
        onClick = {
          when (item.id) {
            "radar" -> onNavigateToRadar()
            "groups" -> onNavigateToGroups()
            "events" -> onNavigateToEvents()
            "collab" -> onNavigateToCollab()
            "live" -> onNavigateToLiveStage()
            "messenger" -> onNavigateToMessenger()
            "onboarding" -> onOpenOnboarding()
            else -> onNavigateToProfile()
          }
        }
      )
    }

    // 5. Settings, Dark Mode, & Security Controls
    item(span = { GridItemSpan(2) }) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = "Settings & Preferences",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        // Dark Mode Toggle Tile
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (userProfile.darkModePreference == "DARK") Icons.Default.Brightness4 else Icons.Default.Brightness7,
                contentDescription = null,
                tint = FacebookBlue,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Dark Mode",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Theme: ${userProfile.darkModePreference}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Switch(
              checked = userProfile.darkModePreference == "DARK",
              onCheckedChange = { isDark ->
                onToggleDarkMode(if (isDark) "DARK" else "LIGHT")
              },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = FacebookBlue
              )
            )
          }
        }

        // Room Database Sync Tile
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (syncState == SyncState.SYNCING_IN_PROGRESS) Icons.Default.CloudSync else Icons.Default.CloudDone,
                contentDescription = null,
                tint = SecondaryLight,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Local Room Database Sync",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = if (userProfile.offlineModeActive) "Offline Mode Active" else "Synced with local cache",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clickable { onTriggerSync() }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = "Sync Now",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = FacebookBlue
              )
            }
          }
        }

        // Security & Cryptography Info
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = Color(0xFF10B981),
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "End-to-End Encryption Fingerprint",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = userProfile.encryptionKeyFingerprint,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun MenuShortcutCard(
  item: MenuShortcutItem,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .height(90.dp)
      .clickable { onClick() }
      .testTag("menu_shortcut_${item.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(
          imageVector = item.icon,
          contentDescription = item.title,
          tint = item.iconColor,
          modifier = Modifier.size(24.dp)
        )

        if (item.badgeText != null) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = item.iconColor.copy(alpha = 0.15f),
            modifier = Modifier.padding(2.dp)
          ) {
            Text(
              text = item.badgeText,
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              ),
              color = item.iconColor,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      Text(
        text = item.title,
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}
