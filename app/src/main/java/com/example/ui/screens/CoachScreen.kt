package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProfileEntity
import com.example.data.PurchaseAdviceEntity
import com.example.ui.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachScreen(
    viewModel: MoneyViewModel,
    modifier: Modifier = Modifier
) {
    val advices by viewModel.advices.collectAsState()
    val coachResult by viewModel.coachResult.collectAsState()
    val isAnalyzing by viewModel.isAnalyzingCoach.collectAsState()
    val profileOpt by viewModel.profile.collectAsState()
    val profile = profileOpt ?: ProfileEntity()

    val symbol = profile.currencySymbol

    // Form states
    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Shopping") }
    var selectedUrgency by remember { mutableStateOf("Somewhat") } // "Very Urgent", "Somewhat", "Not Urgent"
    var selectedNeedWant by remember { mutableStateOf("want") } // "need", "want"
    var purchaseReason by remember { mutableStateOf("") }

    val categories = listOf("Shopping", "Food", "Entertainment", "Travel", "Bills", "Health", "Education", "Other")
    val urgencies = listOf("Very Urgent", "Somewhat", "Not Urgent")

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (isAnalyzing) {
            // Processing Overlay with animated thinking orb
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OrbThinkingIndicator()
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "AI Money Coach is thinking...",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                // Rotating status messages for supreme finish
                val messageIndex = (System.currentTimeMillis() / 1500 % 4).toInt()
                val statuses = listOf(
                    "Calculating life-hours cost equivalent...",
                    "Checking active goal contributions...",
                    "Evaluating current savings cushion...",
                    "Structuring optimized savings plan..."
                )
                Text(
                    text = statuses.getOrElse(messageIndex) { "Analyzing affordability index..." },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        } else if (coachResult != null) {
            // Advice Report Sheet Screen
            val result = coachResult!!
            val scoreColor = when {
                result.score >= 75 -> PrimaryGreen
                result.score >= 50 -> SecondaryBlue
                else -> AlertRed
            }
            val badgeBg = scoreColor.copy(alpha = 0.1f)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.clearCoachResult() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                        Text(
                            text = "AI Coach Verdict",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(48.dp)) // visual balance
                    }
                }

                // Core decision badge & title
                item {
                    PremiumCard(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        elevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = result.itemName.uppercase(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = result.price.toNiceString(symbol),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            // BUY / WAIT / AVOID Badge
                            Surface(
                                color = badgeBg,
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                                ) {
                                    Icon(
                                        imageVector = when (result.recommendation) {
                                            "BUY" -> Icons.Default.CheckCircle
                                            "WAIT" -> Icons.Default.HourglassEmpty
                                            else -> Icons.Default.Cancel
                                        },
                                        contentDescription = result.recommendation,
                                        tint = scoreColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = when (result.recommendation) {
                                            "BUY" -> "BUY DECISION"
                                            "WAIT" -> "WAIT & SAVE"
                                            else -> "AVOID PURCHASE"
                                        },
                                        color = scoreColor,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }

                // Gauge & Details row
                item {
                    PremiumCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PremiumHealthGauge(
                                score = result.score,
                                modifier = Modifier.weight(1.1f)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1.2f)) {
                                Text(
                                    text = "AFFORDABILITY SCORE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = when {
                                        result.score >= 75 -> "Safely budgeted"
                                        result.score >= 50 -> "Delayed choice is better"
                                        else -> "Puts budget in jeopardy"
                                    },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = scoreColor
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Evaluated against your work-hours, goal delays, and want-to-need index.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                // Metric Equivalence Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Work hours required card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(1f)
                                .shadow(2.dp, RoundedCornerShape(20.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Work,
                                    contentDescription = "Work hours",
                                    tint = SecondaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "${result.workHours} Hrs",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Working labor required to earn this price tag.",
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Dream goal delay card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(1f)
                                .shadow(2.dp, RoundedCornerShape(20.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Savings,
                                    contentDescription = "Goal delay",
                                    tint = AccentYellow,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "${result.goalDelayDays} Days",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Delay caused to your savings dreams.",
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Want vs Need splits
                item {
                    PremiumCard {
                        Text(
                            text = "NEED VS WANT PROFILE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Dual progress bar split
                        val wantProgress = result.needWantRatio / 100f
                        val needProgress = 1f - wantProgress
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Need: ${(needProgress * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                            Text(
                                text = "Want: ${(wantProgress * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentYellow
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        PremiumProgressBar(
                            progress = needProgress,
                            progressColor = PrimaryGreen,
                            trackColor = AccentYellow,
                            height = 10.dp
                        )
                    }
                }

                // AI Suggested plan card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Plan",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Optimized Action Plan",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = result.planSuggestion,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Reset Button
                item {
                    Button(
                        onClick = { viewModel.clearCoachResult() },
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground)
                    ) {
                        Text("Analyze Another Item", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        } else {
            // Form entry screen
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(vertical = 10.dp)) {
                        Text(
                            text = "Before You Buy AI",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Think before you spend. Save before you regret.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                item {
                    PremiumCard(elevation = 2.dp) {
                        Text(
                            text = "COACH ADVICE REQUEST",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Item name input
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            label = { Text("What do you want to buy?") },
                            placeholder = { Text("e.g. iPhone, Nike shoes, Designer jacket") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("coach_item_input")
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Price and Category Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = itemPrice,
                                onValueChange = { itemPrice = it },
                                label = { Text("Price tag") },
                                leadingIcon = { Text(symbol, fontWeight = FontWeight.Bold) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("coach_price_input")
                            )

                            // Simple category select simulated with a nice dropdown
                            var categoryExpanded by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = selectedCategory,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Category") },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Dropdown",
                                            modifier = Modifier.clickable { categoryExpanded = true }
                                        )
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                DropdownMenu(
                                    expanded = categoryExpanded,
                                    onDismissRequest = { categoryExpanded = false }
                                ) {
                                    categories.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat) },
                                            onClick = {
                                                selectedCategory = cat
                                                categoryExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Urgency Dropdown selection
                        var urgencyExpanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedUrgency,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("How urgent is this purchase?") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown",
                                        modifier = Modifier.clickable { urgencyExpanded = true }
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(
                                expanded = urgencyExpanded,
                                onDismissRequest = { urgencyExpanded = false }
                            ) {
                                urgencies.forEach { urg ->
                                    DropdownMenuItem(
                                        text = { Text(urg) },
                                        onClick = {
                                            selectedUrgency = urg
                                            urgencyExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Need or Want segment
                        Text(
                            text = "Is this a physical need or psychological want?",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { selectedNeedWant = "need" },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedNeedWant == "need") PrimaryGreen else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (selectedNeedWant == "need") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Text("A Genuine Need", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { selectedNeedWant = "want" },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedNeedWant == "want") AccentYellow else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (selectedNeedWant == "want") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Text("An Impulse Want", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Reason for purchase input
                        OutlinedTextField(
                            value = purchaseReason,
                            onValueChange = { purchaseReason = it },
                            label = { Text("Why do you want it? Be honest...") },
                            placeholder = { Text("e.g. My old phone screen is cracked, or I am sad and want a treat, or peer pressure") },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Analyze Trigger button
                        val priceNum = itemPrice.toDoubleOrNull() ?: 0.0
                        val isFormValid = itemName.isNotEmpty() && priceNum > 0.0
                        Button(
                            onClick = {
                                viewModel.analyzePurchase(
                                    itemName = itemName,
                                    price = priceNum,
                                    category = selectedCategory,
                                    urgency = selectedUrgency,
                                    reason = purchaseReason,
                                    needWant = selectedNeedWant
                                )
                            },
                            enabled = isFormValid,
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("coach_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Consult AI Coach", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }

                // Coach Logs history title
                if (advices.isNotEmpty()) {
                    item {
                        Text(
                            text = "Previous Consultations",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(advices) { log ->
                        val color = when (log.recommendation) {
                            "BUY" -> PrimaryGreen
                            "WAIT" -> SecondaryBlue
                            else -> AlertRed
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(1.dp, RoundedCornerShape(18.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(color.copy(alpha = 0.10f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (log.recommendation) {
                                                "BUY" -> Icons.Default.Check
                                                "WAIT" -> Icons.Default.HourglassEmpty
                                                else -> Icons.Default.Block
                                            },
                                            contentDescription = log.recommendation,
                                            tint = color,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = log.itemName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(1.dp))
                                        Text(
                                            text = "${log.price.toNiceString(symbol)} • Affordability score: ${log.score}/100",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                Text(
                                    text = log.recommendation,
                                    color = color,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
