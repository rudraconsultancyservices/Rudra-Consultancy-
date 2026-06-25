package com.example.ui

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.AlertRed
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryBlue
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 6.dp,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = modifier
        .shadow(elevation, shape = shape, clip = false)
        .clip(shape)
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    backgroundColor,
                    backgroundColor.copy(alpha = 0.95f)
                )
            )
        )
        .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
        .padding(20.dp)

    Column(
        modifier = cardModifier,
        content = content
    )
}

@Composable
fun MetricRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    labelColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
    valueColor: Color = MaterialTheme.colorScheme.onBackground
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = labelColor,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 15.sp,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PremiumProgressBar(
    progress: Float, // 0f to 1f
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
    progressColor: Color = MaterialTheme.colorScheme.primary,
    height: Dp = 10.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(1000),
        label = "ProgressBarProgress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            progressColor.copy(alpha = 0.8f),
                            progressColor
                        )
                    )
                )
        )
    }
}

@Composable
fun PremiumHealthGauge(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp
) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(1200),
        label = "GaugeScore"
    )

    val progress = animatedScore / 100f
    val color = when {
        score >= 80 -> PrimaryGreen
        score >= 55 -> SecondaryBlue
        score >= 35 -> AccentYellow
        else -> AlertRed
    }

    val status = when {
        score >= 80 -> "Excellent"
        score >= 55 -> "Good"
        score >= 35 -> "Improving"
        else -> "Needs Attention"
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size - 20.dp)) {
            val strokeWidth = 14.dp.toPx()
            
            // Draw track (semi-circle from 150 to 390 degrees)
            drawArc(
                color = Color.LightGray.copy(alpha = 0.25f),
                startAngle = 140f,
                sweepAngle = 260f,
                useCenter = false,
                size = Size(width = size.toPx() - 40.dp.toPx(), height = size.toPx() - 40.dp.toPx()),
                topLeft = Offset(20.dp.toPx(), 20.dp.toPx()),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Draw active progress arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(color.copy(alpha = 0.4f), color),
                    center = Offset(size.toPx() / 2f, size.toPx() / 2f)
                ),
                startAngle = 140f,
                sweepAngle = 260f * progress,
                useCenter = false,
                size = Size(width = size.toPx() - 40.dp.toPx(), height = size.toPx() - 40.dp.toPx()),
                topLeft = Offset(20.dp.toPx(), 20.dp.toPx()),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${score.coerceIn(0, 100)}",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "/100",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = color.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = status,
                    color = color,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CompactHealthGauge(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    strokeWidth: Dp = 5.dp
) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(1200),
        label = "CompactGaugeScore"
    )

    val progress = animatedScore / 100f
    val color = when {
        score >= 80 -> PrimaryGreen
        score >= 55 -> SecondaryBlue
        score >= 35 -> AccentYellow
        else -> AlertRed
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sw = strokeWidth.toPx()
            val radius = (this.size.minDimension - sw) / 2f
            
            // Draw background circle
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.25f),
                radius = radius,
                style = Stroke(width = sw)
            )

            // Draw active progress arc (starting from top, i.e., -90 degrees)
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = sw, cap = StrokeCap.Round)
            )
        }

        Text(
            text = "${score.coerceIn(0, 100)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

@Composable
fun PremiumDonutChart(
    slices: List<Pair<String, Double>>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    size: Dp = 150.dp,
    currencySymbol: String = "₹"
) {
    val total = slices.sumOf { it.second }.coerceAtLeast(1.0)
    
    // Compute sweep angles
    val sweepAngles = slices.map { (it.second / total * 360f).toFloat() }
    
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1200),
        label = "DonutChartLoad"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 18.dp.toPx()
            var startAngle = -90f

            for (i in slices.indices) {
                val sweep = sweepAngles[i] * animatedProgress
                if (sweep > 0f) {
                    drawArc(
                        color = colors.getOrElse(i) { Color.Gray },
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        size = Size(width = size.toPx() - 25.dp.toPx(), height = size.toPx() - 25.dp.toPx()),
                        topLeft = Offset(12.5.dp.toPx(), 12.5.dp.toPx()),
                        style = Stroke(width = strokeWidth)
                    )
                }
                startAngle += sweepAngles[i]
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Total Spend",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$currencySymbol${total.roundToInt()}",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PremiumBarChart(
    values: List<Double>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    height: Dp = 140.dp,
    barColor: Color = MaterialTheme.colorScheme.primary,
    currencySymbol: String = "₹"
) {
    val maxValue = values.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0
    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000),
        label = "BarChartAnimation"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        values.forEachIndexed { index, valD ->
            val fraction = (valD / maxValue).toFloat() * animProgress
            val label = labels.getOrElse(index) { "" }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Value tooltip
                Text(
                    text = if (valD > 0) "$currencySymbol${valD.roundToInt()}" else "-",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Animated column
                Box(
                    modifier = Modifier
                        .fillMaxHeight(fraction.coerceIn(0.01f, 0.9f))
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    barColor,
                                    barColor.copy(alpha = 0.6f)
                                )
                            )
                        )
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Category/Day label
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun OrbThinkingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 90.dp
) {
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "Orb")
    
    val animatedScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = tween(1200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "OrbScale"
    )

    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = tween(1200, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "OrbAlpha"
    )

    Box(
        modifier = modifier
            .size(size * animatedScale)
            .shadow(16.dp, CircleShape, clip = false)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        PrimaryGreen,
                        SecondaryBlue.copy(alpha = animatedAlpha),
                        Color.Transparent
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.45f)
                .clip(CircleShape)
                .background(AccentYellow)
        )
    }
}

// Extension to round double to nice format
fun Double.toNiceString(symbol: String = "₹"): String {
    return if (this % 1.0 == 0.0) {
        "$symbol${this.toLong()}"
    } else {
        "$symbol${String.format("%.2f", this)}"
    }
}
