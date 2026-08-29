package com.example.ui.screens.discovery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventRsvpStatus
import com.example.data.model.HobbyEvent
import com.example.data.model.HobbyGroup
import com.example.ui.components.OrganizerReputationBadge
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoseRed
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.TertiaryDark
import com.example.ui.viewmodel.DiscoveryUiState

@Composable
fun MapDiscoveryScreen(
  groups: List<HobbyGroup>,
  events: List<HobbyEvent>,
  discoveryState: DiscoveryUiState,
  onCategorySelected: (String) -> Unit,
  onSearchQueryChanged: (String) -> Unit,
  onRadiusSelected: (Float) -> Unit,
  onPinGroupSelected: (HobbyGroup?) -> Unit,
  onPinEventSelected: (HobbyEvent?) -> Unit,
  onToggleGroupMembership: (String, Boolean) -> Unit,
  onUpdateEventRsvp: (String, EventRsvpStatus) -> Unit,
  onNavigateToGroupDetail: (String) -> Unit,
  onNavigateToEventDetail: (String) -> Unit,
  onCreateEventClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val categories = listOf(
    "All", "Board Games", "Urban Gardening", "Drone & Tech", "Synth & Audio", "Woodworking", "Astronomy"
  )

  val filteredGroups = groups.filter { group ->
    (discoveryState.selectedCategory == "All" || group.category.equals(discoveryState.selectedCategory, ignoreCase = true)) &&
        (discoveryState.searchKeyword.isEmpty() || group.name.contains(discoveryState.searchKeyword, ignoreCase = true) || group.tags.any { it.contains(discoveryState.searchKeyword, ignoreCase = true) })
  }

  val filteredEvents = events.filter { event ->
    (discoveryState.selectedCategory == "All" || event.category.equals(discoveryState.selectedCategory, ignoreCase = true)) &&
        (discoveryState.searchKeyword.isEmpty() || event.title.contains(discoveryState.searchKeyword, ignoreCase = true) || event.locationName.contains(discoveryState.searchKeyword, ignoreCase = true))
  }

  // Animation for pulsating radar scan
  val infiniteTransition = rememberInfiniteTransition(label = "radar_pulse")
  val pulseRadius by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(3500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "radar_circle"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // --- Interactive Radar Map Canvas ---
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
      val canvasWidth = constraints.maxWidth.toFloat()
      val canvasHeight = constraints.maxHeight.toFloat()
      val centerX = canvasWidth / 2f
      val centerY = canvasHeight / 2f

      val primaryColor = MaterialTheme.colorScheme.primary
      val secondaryColor = MaterialTheme.colorScheme.secondary
      val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
      val bgSurface = MaterialTheme.colorScheme.surface

      Canvas(
        modifier = Modifier
          .fillMaxSize()
          .pointerInput(Unit) {
            detectTapGestures {
              // Dismiss selection on blank area tap
              onPinGroupSelected(null)
              onPinEventSelected(null)
            }
          }
      ) {
        val maxRadius = minOf(size.width, size.height) * 0.45f

        // Draw Map Grid Background Lines
        val step = 60.dp.toPx()
        var x = 0f
        while (x < size.width) {
          drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
          )
          x += step
        }
        var y = 0f
        while (y < size.height) {
          drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
          )
          y += step
        }

        // Concentric Radar Distance Rings
        drawCircle(
          color = primaryColor.copy(alpha = 0.10f),
          radius = maxRadius * 0.33f,
          center = Offset(centerX, centerY),
          style = Stroke(width = 1.5f)
        )
        drawCircle(
          color = primaryColor.copy(alpha = 0.08f),
          radius = maxRadius * 0.66f,
          center = Offset(centerX, centerY),
          style = Stroke(width = 1.5f)
        )
        drawCircle(
          color = primaryColor.copy(alpha = 0.06f),
          radius = maxRadius,
          center = Offset(centerX, centerY),
          style = Stroke(width = 2f)
        )

        // Pulsing Scan Ring
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(primaryColor.copy(alpha = 0.15f * (1f - pulseRadius)), Color.Transparent),
            center = Offset(centerX, centerY),
            radius = maxRadius * pulseRadius
          ),
          radius = maxRadius * pulseRadius,
          center = Offset(centerX, centerY)
        )

        // Center "User Location" Marker
        drawCircle(
          color = SkyBlue.copy(alpha = 0.3f),
          radius = 24.dp.toPx(),
          center = Offset(centerX, centerY)
        )
        drawCircle(
          color = SkyBlue,
          radius = 8.dp.toPx(),
          center = Offset(centerX, centerY)
        )
        drawCircle(
          color = Color.White,
          radius = 3.dp.toPx(),
          center = Offset(centerX, centerY)
        )
      }

      // --- Overlay Interactive Group & Event Pins ---
      // Map offsets relative to center
      val pinPositions = listOf(
        Pair(-0.25f, -0.28f),
        Pair(0.28f, -0.20f),
        Pair(-0.30f, 0.22f),
        Pair(0.18f, 0.26f),
        Pair(-0.10f, 0.35f),
        Pair(0.32f, 0.10f)
      )

      val density = LocalDensity.current

      filteredGroups.forEachIndexed { index, group ->
        val pos = pinPositions.getOrElse(index) { Pair(0.1f * (index + 1), -0.1f * (index + 1)) }
        val pinX = centerX + (pos.first * canvasWidth * 0.7f)
        val pinY = centerY + (pos.second * canvasHeight * 0.55f)
        val isSelected = discoveryState.selectedPinGroup?.id == group.id

        val offsetX = with(density) { pinX.toDp() } - 24.dp
        val offsetY = with(density) { pinY.toDp() } - 24.dp

        Box(
          modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(48.dp)
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            .border(
              width = if (isSelected) 3.dp else 1.5.dp,
              color = if (isSelected) EmeraldGreen else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
              shape = CircleShape
            )
            .clickable { onPinGroupSelected(group) }
            .semantics { contentDescription = "Map pin for group ${group.name}" }
            .testTag("map_pin_group_${group.id}"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = group.iconEmoji,
            fontSize = 20.sp
          )
        }
      }

      // Live event pins with pulsing badge
      filteredEvents.forEachIndexed { index, event ->
        val pos = pinPositions.getOrElse(index + 2) { Pair(-0.15f * index, 0.15f * index) }
        val shiftPx = with(density) { 30.dp.toPx() }
        val pinX = centerX + (pos.first * canvasWidth * 0.65f) + shiftPx
        val pinY = centerY + (pos.second * canvasHeight * 0.50f) - shiftPx
        val isSelected = discoveryState.selectedPinEvent?.id == event.id

        val offsetX = with(density) { pinX.toDp() } - 24.dp
        val offsetY = with(density) { pinY.toDp() } - 24.dp

        Box(
          modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (event.isLiveNow) RoseRed.copy(alpha = 0.9f) else MaterialTheme.colorScheme.secondaryContainer)
            .border(
              width = if (isSelected) 3.dp else 1.5.dp,
              color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.5f),
              shape = RoundedCornerShape(14.dp)
            )
            .clickable { onPinEventSelected(event) }
            .semantics { contentDescription = "Event pin for ${event.title}" }
            .testTag("map_pin_event_${event.id}"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = event.coverIcon,
            fontSize = 20.sp
          )
        }
      }
    }

    // --- Top Search & Category Filter Controls ---
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Search Bar
      Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          OutlinedTextField(
            value = discoveryState.searchKeyword,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text("Discover local interest guilds & events...", fontSize = 14.sp) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color.Transparent,
              unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("map_search_input")
          )
          if (discoveryState.searchKeyword.isNotEmpty()) {
            IconButton(
              onClick = { onSearchQueryChanged("") },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(Icons.Default.Close, contentDescription = "Clear search", modifier = Modifier.size(18.dp))
            }
          }
        }
      }

      // Horizontal Category Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        categories.forEach { cat ->
          val isSelected = discoveryState.selectedCategory.equals(cat, ignoreCase = true)
          FilterChip(
            selected = isSelected,
            onClick = { onCategorySelected(cat) },
            label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.testTag("filter_chip_$cat")
          )
        }
      }

      // Radius selector chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Radar Radius:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
        listOf(5f to "5 km", 15f to "15 km", 30f to "30 km", 50f to "50 km").forEach { (radius, label) ->
          val isSelected = discoveryState.searchRadiusKm == radius
          Surface(
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .clickable { onRadiusSelected(radius) }
              .testTag("radius_chip_$label")
          ) {
            Text(
              text = label,
              fontSize = 10.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }
    }

    // --- Floating Discovery Action Buttons ---
    Column(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .padding(end = 16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      FloatingActionButton(
        onClick = {
          onRadiusSelected(15f)
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier
          .size(48.dp)
          .testTag("recenter_map_button")
      ) {
        Icon(Icons.Default.MyLocation, contentDescription = "Recenter on my location", modifier = Modifier.size(22.dp))
      }

      FloatingActionButton(
        onClick = onCreateEventClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
          .size(48.dp)
          .testTag("create_event_map_button")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Plan Event or Guild", modifier = Modifier.size(24.dp))
      }
    }

    // --- Bottom Details Card (Animated on Pin Selection) ---
    AnimatedVisibility(
      visible = discoveryState.selectedPinGroup != null || discoveryState.selectedPinEvent != null,
      enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
      exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(16.dp)
    ) {
      if (discoveryState.selectedPinGroup != null) {
        val group = discoveryState.selectedPinGroup
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("map_group_bottom_card")
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Top
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(group.coverColorHex).copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(group.iconEmoji, fontSize = 24.sp)
                }
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                      text = group.name,
                      fontSize = 16.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                  }
                  Text(
                    text = "${group.category} • ${group.membersCount} Members • ${if (group.isPrivate) "Private Guild" else "Open Community"}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              IconButton(
                onClick = { onPinGroupSelected(null) },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(Icons.Default.Close, contentDescription = "Close Card", modifier = Modifier.size(18.dp))
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Organizer reputation summary
            OrganizerReputationBadge(
              organizerName = group.organizerName,
              reputationScore = group.organizerReputation,
              isVerified = group.isVerifiedOrganizer,
              compact = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = group.description,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedButton(
                onClick = { onNavigateToGroupDetail(group.id) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("view_group_details_button")
              ) {
                Text("Explore Guild", fontSize = 13.sp)
              }

              Button(
                onClick = { onToggleGroupMembership(group.id, group.isMember) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (group.isMember) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                  .weight(1f)
                  .testTag("join_leave_group_button")
              ) {
                Icon(
                  imageVector = if (group.isMember) Icons.Default.Check else Icons.Default.Add,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (group.isMember) "Joined" else "Join Guild", fontSize = 13.sp)
              }
            }
          }
        }
      } else if (discoveryState.selectedPinEvent != null) {
        val event = discoveryState.selectedPinEvent
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("map_event_bottom_card")
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Top
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (event.isLiveNow) RoseRed.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer),
                  contentAlignment = Alignment.Center
                ) {
                  Text(event.coverIcon, fontSize = 24.sp)
                }
                Column {
                  if (event.isLiveNow) {
                    Text("● LIVE NOW", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RoseRed)
                  }
                  Text(
                    text = event.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = "${event.dateFormatted} • ${event.timeFormatted}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                  )
                }
              }

              IconButton(
                onClick = { onPinEventSelected(null) },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(Icons.Default.Close, contentDescription = "Close Card", modifier = Modifier.size(18.dp))
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
              Text(event.locationName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Hosted by ${event.organizerName} (${event.organizerReputation}% reliability) • ${event.currentRsvpCount}/${event.maxAttendees} Attending",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedButton(
                onClick = { onNavigateToEventDetail(event.id) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("view_event_details_button")
              ) {
                Text("Event Details", fontSize = 13.sp)
              }

              val isGoing = event.userRsvp == EventRsvpStatus.GOING
              Button(
                onClick = {
                  val next = if (isGoing) EventRsvpStatus.NONE else EventRsvpStatus.GOING
                  onUpdateEventRsvp(event.id, next)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isGoing) EmeraldGreen else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                  .weight(1f)
                  .testTag("rsvp_event_button")
              ) {
                Text(if (isGoing) "Going ✓" else "RSVP Going", fontSize = 13.sp)
              }
            }
          }
        }
      }
    }
  }
}
