package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VerifiedUser
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
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TertiaryDark

@Composable
fun OrganizerReputationBadge(
  organizerName: String,
  reputationScore: Int,
  isVerified: Boolean,
  compact: Boolean = false,
  onEndorseClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val tierName = when {
    reputationScore >= 98 -> "Master Host"
    reputationScore >= 95 -> "Trusted Organizer"
    reputationScore >= 90 -> "Reliable Planner"
    else -> "Community Member"
  }

  val badgeColor = when {
    reputationScore >= 95 -> EmeraldGreen
    reputationScore >= 90 -> MaterialTheme.colorScheme.primary
    else -> MaterialTheme.colorScheme.secondary
  }

  if (compact) {
    Surface(
      color = badgeColor.copy(alpha = 0.12f),
      shape = RoundedCornerShape(12.dp),
      modifier = modifier
        .semantics { contentDescription = "Organizer $organizerName reliability score $reputationScore%" }
        .testTag("organizer_reputation_compact_$organizerName")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        if (isVerified) {
          Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = "Verified Organizer",
            tint = badgeColor,
            modifier = Modifier.size(14.dp)
          )
        }
        Text(
          text = "$reputationScore% Trust",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = badgeColor
        )
      }
    }
  } else {
    Surface(
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
      shape = RoundedCornerShape(16.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.25f)),
      modifier = modifier
        .semantics { contentDescription = "Organizer reputation card for $organizerName" }
        .testTag("organizer_reputation_card_$organizerName")
    ) {
      Row(
        modifier = Modifier
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(badgeColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isVerified) Icons.Default.CheckCircle else Icons.Default.Star,
              contentDescription = null,
              tint = badgeColor,
              modifier = Modifier.size(22.dp)
            )
          }

          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = organizerName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              if (isVerified) {
                Icon(
                  imageVector = Icons.Default.VerifiedUser,
                  contentDescription = "Verified",
                  tint = EmeraldGreen,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
            Text(
              text = "$tierName • $reputationScore% Reliability",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        if (onEndorseClick != null) {
          Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable { onEndorseClick() }
              .testTag("endorse_organizer_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = "Endorse Organizer",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = "Endorse",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
        }
      }
    }
  }
}
