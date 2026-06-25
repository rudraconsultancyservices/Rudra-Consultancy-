package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import com.example.data.ChallengeEntity
import com.example.data.GoalEntity
import com.example.data.ProfileEntity
import com.example.ui.*
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.AlertRed
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    viewModel: MoneyViewModel,
    onOpenAddGoal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goals by viewModel.goals.collectAsState()
    val challenges by viewModel.challenges.collectAsState()
    val profileOpt by viewModel.profile.collectAsState()
    val profile = profileOpt ?: ProfileEntity()
    val symbol = profile.currencySymbol

    // Inline allocation modal state
    var selectedAllocationGoal by remember { mutableStateOf<GoalEntity?>(null) }
    var allocationAmount by remember { mutableStateOf("") }

    val activeChallenges = challenges.filter { it.status == "active" }
    val completedChallenges = challenges.filter { it.status == "completed" }

    val streakCount = completedChallenges.size.coerceAtLeast(0) // Simulate streak from completed items

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp)
    ) {
        // Goals Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dream Boards",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Visualize and save for what matters.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = onOpenAddGoal,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("add_goal_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Goal")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Goals List
        if (goals.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = "Savings",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Dream Boards yet.",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Create a goal for a laptop, vacation, or emergency fund, and start building towards it!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(goals) { goal ->
                val ratio = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
                PremiumCard(elevation = 3.dp) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = goal.emoji, fontSize = 24.sp)
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = goal.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${goal.category} • Priority: ${goal.priority}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.deleteGoal(goal) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete Goal",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progress: ${goal.currentAmount.toNiceString(symbol)} / ${goal.targetAmount.toNiceString(symbol)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${(ratio * 100).toInt()}%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (ratio >= 1.0f) PrimaryGreen else SecondaryBlue
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        PremiumProgressBar(
                            progress = ratio,
                            progressColor = if (ratio >= 1.0f) PrimaryGreen else SecondaryBlue
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Target: ${goal.targetDate} (Suggested: $symbol${goal.monthlyContribution.toInt()}/mo)",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            // Inline allocation trigger
                            Text(
                                text = "Add Money",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                    .clickable { selectedAllocationGoal = goal }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Daily Challenges Section Header
        item {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    text = "Habits & Streaks",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Daily gamified challenges to lock up savings.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Streak status row
        item {
            PremiumCard(backgroundColor = AccentYellow.copy(alpha = 0.08f), elevation = 2.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔥", fontSize = 36.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "$streakCount Days Savings Streak!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Every completed challenge earns you coins and logs cash straight into your vaults.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Active Challenges
        if (activeChallenges.isNotEmpty()) {
            item {
                Text(
                    text = "Active Today",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            items(activeChallenges) { challenge ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(AccentYellow.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = "Challenge",
                                        tint = AccentYellow,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = challenge.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "Potential Save: $symbol${challenge.amount.toInt()} • Reward: +${challenge.rewardCoins} Coins",
                                        fontSize = 11.sp,
                                        color = PrimaryGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.skipChallenge(challenge) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(0.8f)
                            ) {
                                Text("Skip", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.completeChallenge(challenge) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Done", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Completed", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Completed Achievements List
        if (completedChallenges.isNotEmpty()) {
            item {
                Text(
                    text = "Achieved Badges",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            items(completedChallenges) { log ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(text = "⭐", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = log.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Saved $symbol${log.amount.toInt()} • Earned +${log.rewardCoins} Coins",
                                    fontSize = 11.sp,
                                    color = PrimaryGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "REWARDED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryGreen
                        )
                    }
                }
            }
        }
    }

    // Inline Allocation Alert Dialog modal
    if (selectedAllocationGoal != null) {
        val goal = selectedAllocationGoal!!
        AlertDialog(
            onDismissRequest = { selectedAllocationGoal = null; allocationAmount = "" },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = goal.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Fund: ${goal.name}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Allocate savings from your checking bank or pocket directly towards this dream goal.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = allocationAmount,
                        onValueChange = { allocationAmount = it },
                        label = { Text("Allocation Amount") },
                        leadingIcon = { Text(symbol, fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amountNum = allocationAmount.toDoubleOrNull() ?: 0.0
                        if (amountNum > 0.0) {
                            viewModel.addMoneyToGoal(goal, amountNum)
                        }
                        selectedAllocationGoal = null
                        allocationAmount = ""
                    },
                    enabled = allocationAmount.toDoubleOrNull() ?: 0.0 > 0.0
                ) {
                    Text("Allocate", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedAllocationGoal = null; allocationAmount = "" }) {
                    Text("Cancel")
                }
            }
        )
    }
}
