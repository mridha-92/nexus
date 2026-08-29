package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class OnboardingStep(
  val emoji: String,
  val title: String,
  val description: String,
  val featureHighlight: String
)

@Composable
fun OnboardingTutorialDialog(
  onDismiss: () -> Unit
) {
  val steps = listOf(
    OnboardingStep(
      emoji = "🗺️",
      title = "Interactive Radar Discovery",
      description = "Explore nearby hobbyist guilds, workshops, and live sessions with real-time map pins and category filters.",
      featureHighlight = "Adjust radar distance from 5km to 50km to locate local makers and enthusiasts."
    ),
    OnboardingStep(
      emoji = "🛡️",
      title = "Organizer Reputation System",
      description = "Verify organizer reliability through verified credentials, community trust percentages, and peer reviews.",
      featureHighlight = "Rest easy knowing group hosts and meetups meet community standards."
    ),
    OnboardingStep(
      emoji = "🔒",
      title = "End-to-End Encrypted Chats",
      description = "Communicate privately with local peers and guilds using AES-256 encrypted messaging and secure media sharing.",
      featureHighlight = "Share photos, voice notes, blueprints, and location pins securely."
    ),
    OnboardingStep(
      emoji = "📝",
      title = "Real-Time Collaborative Workspace",
      description = "Co-author group blueprints, track project milestone deadlines, and collaborate in live audio/video lounges.",
      featureHighlight = "Full offline-first caching ensures your notes and checklists remain accessible anytime."
    )
  )

  var currentStepIndex by remember { mutableIntStateOf(0) }
  val currentStep = steps[currentStepIndex]

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Progress Dots
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            steps.forEachIndexed { idx, _ ->
              Box(
                modifier = Modifier
                  .size(if (idx == currentStepIndex) 16.dp else 8.dp, 8.dp)
                  .clip(CircleShape)
                  .background(if (idx == currentStepIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
              )
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
          }
        }

        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center
        ) {
          Text(currentStep.emoji, fontSize = 36.sp)
        }

        Text(
          text = currentStep.title,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = currentStep.description,
          fontSize = 13.sp,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 18.sp
        )

        Surface(
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "💡 ${currentStep.featureHighlight}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(10.dp)
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (currentStepIndex < steps.size - 1) {
            currentStepIndex++
          } else {
            onDismiss()
          }
        },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("onboarding_next_button")
      ) {
        Text(if (currentStepIndex < steps.size - 1) "Next" else "Get Started")
      }
    },
    dismissButton = {
      if (currentStepIndex > 0) {
        TextButton(onClick = { currentStepIndex-- }) {
          Text("Back")
        }
      } else {
        TextButton(onClick = onDismiss) {
          Text("Skip")
        }
      }
    }
  )
}
