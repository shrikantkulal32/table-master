package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.WrongRed

/**
 * Quiz option card with static green highlighting for the correct answer and red for wrong answer.
 */
@Composable
fun QuizOptionCard(
    optionNumber: String,
    answerValue: Int,
    isSelected: Boolean,
    isCorrectOption: Boolean,
    isAnswerChecked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Clean static styling: no blinking, reliable static green and red feedback
    val backgroundColor = when {
        !isAnswerChecked -> MaterialTheme.colorScheme.surface
        isCorrectOption -> CorrectGreen.copy(alpha = 0.22f)
        isSelected && !isCorrectOption -> WrongRed.copy(alpha = 0.22f)
        else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
    }

    val borderColor = when {
        !isAnswerChecked -> MaterialTheme.colorScheme.outlineVariant
        isCorrectOption -> CorrectGreen
        isSelected && !isCorrectOption -> WrongRed
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = borderColor,
        animationSpec = tween(durationMillis = 200),
        label = "borderColorAnim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .testTag("option_card_$optionNumber")
            .clickable(enabled = !isAnswerChecked, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(if (isAnswerChecked && (isCorrectOption || isSelected)) 2.5.dp else 1.dp, animatedBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected || (isAnswerChecked && isCorrectOption)) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Option label badge (A, B, C, D)
                val badgeBg = when {
                    !isAnswerChecked -> MaterialTheme.colorScheme.primaryContainer
                    isCorrectOption -> CorrectGreen
                    isSelected && !isCorrectOption -> WrongRed
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                val badgeText = when {
                    !isAnswerChecked -> MaterialTheme.colorScheme.onPrimaryContainer
                    isCorrectOption -> Color.Black
                    isSelected && !isCorrectOption -> Color.White
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = optionNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = badgeText
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // The answer number
                Text(
                    text = answerValue.toString(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = when {
                        !isAnswerChecked -> MaterialTheme.colorScheme.onSurface
                        isCorrectOption -> CorrectGreen
                        isSelected && !isCorrectOption -> WrongRed
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    }
                )
            }

            // Indicator icon when answered
            if (isAnswerChecked) {
                if (isCorrectOption) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CorrectGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Correct",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(WrongRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Wrong",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Stepper and Slider component for setting range criteria.
 */
@Composable
fun RangeCriteriaControl(
    title: String,
    subtitle: String,
    minValue: Int,
    maxValue: Int,
    minLimit: Int = 1,
    maxLimit: Int = 50,
    onRangeChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "$minValue to $maxValue",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Steppers for Start & End
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // From:
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("From:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { if (minValue > minLimit) onRangeChange(minValue - 1, maxValue) },
                        enabled = minValue > minLimit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease min", modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = "$minValue",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(
                        onClick = { if (minValue < maxValue) onRangeChange(minValue + 1, maxValue) },
                        enabled = minValue < maxValue,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase min", modifier = Modifier.size(18.dp))
                    }
                }

                // Till:
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Till:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { if (maxValue > minValue) onRangeChange(minValue, maxValue - 1) },
                        enabled = maxValue > minValue,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease max", modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = "$maxValue",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(
                        onClick = { if (maxValue < maxLimit) onRangeChange(minValue, maxValue + 1) },
                        enabled = maxValue < maxLimit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase max", modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Slider for quick adjustment of the upper bound
            Slider(
                value = maxValue.toFloat(),
                onValueChange = { newVal ->
                    val rounded = newVal.toInt().coerceIn(minValue, maxLimit)
                    onRangeChange(minValue, rounded)
                },
                valueRange = minLimit.toFloat()..maxLimit.toFloat(),
                steps = (maxLimit - minLimit) - 1,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Metric summary card for Scoreboard.
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
