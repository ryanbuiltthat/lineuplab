package com.lineuplab.app.ui.lineup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lineuplab.app.domain.model.FormationSlot
import com.lineuplab.app.domain.sport.SoccerConfig
import com.lineuplab.app.ui.theme.BadgeBlue
import com.lineuplab.app.ui.theme.PitchGreen
import com.lineuplab.app.ui.theme.PitchGreenDark

private const val LINE_ALPHA = 0.85f
private val BADGE_SIZE = 56.dp
private val BADGE_LABEL_HEIGHT = 20.dp
private val BADGE_COLUMN_WIDTH = BADGE_SIZE + 24.dp

/**
 * Fullscreen soccer pitch: draws the field markings on a [Canvas] scaled to
 * fit the available space at the sport's aspect ratio, then overlays a
 * tappable badge for each formation slot at its normalized coordinate.
 * Own goal renders at the bottom (y=0), attacking direction is upward.
 */
@Composable
fun SoccerFieldView(
    slots: List<FormationSlot>,
    assignments: Map<Int, String>,
    onPositionClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.background(PitchGreenDark),
        contentAlignment = Alignment.Center,
    ) {
        val density = LocalDensity.current
        val maxWidthPx = with(density) { maxWidth.toPx() }
        val maxHeightPx = with(density) { maxHeight.toPx() }
        val aspect = SoccerConfig.fieldAspectRatio

        var fieldWidthPx = maxWidthPx
        var fieldHeightPx = fieldWidthPx / aspect
        if (fieldHeightPx > maxHeightPx) {
            fieldHeightPx = maxHeightPx
            fieldWidthPx = fieldHeightPx * aspect
        }
        val fieldWidthDp = with(density) { fieldWidthPx.toDp() }
        val fieldHeightDp = with(density) { fieldHeightPx.toDp() }

        Box(modifier = Modifier.size(fieldWidthDp, fieldHeightDp).background(PitchGreen)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawFieldMarkings()
            }
            slots.forEach { slot ->
                PositionBadge(
                    slot = slot,
                    playerName = assignments[slot.positionNumber],
                    fieldWidthPx = fieldWidthPx,
                    fieldHeightPx = fieldHeightPx,
                    onClick = { onPositionClick(slot.positionNumber) },
                )
            }
        }
    }
}

private fun DrawScope.drawFieldMarkings() {
    val strokeWidth = 3.dp.toPx()
    val color = Color.White.copy(alpha = LINE_ALPHA)
    val w = size.width
    val h = size.height

    drawRect(color = color, size = size, style = Stroke(width = strokeWidth))
    drawLine(color, Offset(0f, h / 2f), Offset(w, h / 2f), strokeWidth)

    val circleRadius = w * 0.12f
    drawCircle(color, radius = circleRadius, center = Offset(w / 2f, h / 2f), style = Stroke(strokeWidth))
    drawCircle(color, radius = 3.dp.toPx(), center = Offset(w / 2f, h / 2f))

    val penaltyBoxWidth = w * 0.6f
    val penaltyBoxHeight = h * 0.16f
    val goalBoxWidth = w * 0.3f
    val goalBoxHeight = h * 0.06f

    // Own goal (bottom, y=0)
    drawRect(
        color = color,
        topLeft = Offset((w - penaltyBoxWidth) / 2f, h - penaltyBoxHeight),
        size = Size(penaltyBoxWidth, penaltyBoxHeight),
        style = Stroke(strokeWidth),
    )
    drawRect(
        color = color,
        topLeft = Offset((w - goalBoxWidth) / 2f, h - goalBoxHeight),
        size = Size(goalBoxWidth, goalBoxHeight),
        style = Stroke(strokeWidth),
    )

    // Opponent goal (top, y=1)
    drawRect(
        color = color,
        topLeft = Offset((w - penaltyBoxWidth) / 2f, 0f),
        size = Size(penaltyBoxWidth, penaltyBoxHeight),
        style = Stroke(strokeWidth),
    )
    drawRect(
        color = color,
        topLeft = Offset((w - goalBoxWidth) / 2f, 0f),
        size = Size(goalBoxWidth, goalBoxHeight),
        style = Stroke(strokeWidth),
    )
}

@Composable
private fun PositionBadge(
    slot: FormationSlot,
    playerName: String?,
    fieldWidthPx: Float,
    fieldHeightPx: Float,
    onClick: () -> Unit,
) {
    val density = LocalDensity.current
    val badgeSizePx = with(density) { BADGE_SIZE.toPx() }
    val columnWidthPx = with(density) { BADGE_COLUMN_WIDTH.toPx() }
    val columnHeightPx = badgeSizePx + with(density) { BADGE_LABEL_HEIGHT.toPx() }
    val centerXPx = slot.x * fieldWidthPx
    val centerYPx = (1f - slot.y) * fieldHeightPx

    // Center the whole column (badge + label) on the slot's coordinate, not
    // just the circle, then clamp so wide labels near the field edges never
    // render off-screen.
    val rawOffsetXPx = centerXPx - columnWidthPx / 2f
    val clampedOffsetXPx = rawOffsetXPx.coerceIn(0f, (fieldWidthPx - columnWidthPx).coerceAtLeast(0f))
    val rawOffsetYPx = centerYPx - badgeSizePx / 2f
    val clampedOffsetYPx = rawOffsetYPx.coerceIn(0f, (fieldHeightPx - columnHeightPx).coerceAtLeast(0f))

    val offsetXDp = with(density) { clampedOffsetXPx.toDp() }
    val offsetYDp = with(density) { clampedOffsetYPx.toDp() }

    Column(
        modifier = Modifier
            .offset(x = offsetXDp, y = offsetYDp)
            .width(BADGE_COLUMN_WIDTH)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            shape = CircleShape,
            color = if (playerName != null) BadgeBlue else Color.White.copy(alpha = 0.3f),
            border = BorderStroke(2.dp, Color.White),
            modifier = Modifier.size(BADGE_SIZE),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = slot.positionNumber.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Text(
            text = playerName ?: "Tap to assign",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}
