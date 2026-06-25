package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChallengeEntity
import com.example.data.CoachEngine
import com.example.data.GoalEntity
import com.example.data.ProfileEntity
import com.example.data.TransactionEntity
import com.example.ui.*
import androidx.compose.ui.draw.shadow
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: MoneyViewModel,
    onOpenAddTransaction: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.transactions.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val challenges by viewModel.challenges.collectAsState()
    val profileOpt by viewModel.profile.collectAsState()
    val profile = profileOpt ?: ProfileEntity()

    val symbol = profile.currencySymbol

    // Filter transactions for current month (June 2026)
    val thisMonthExpenses = transactions
        .filter { it.type == "expense" && it.date.startsWith("2026-06") }
        .sumOf { it.amount }
    
    val thisMonthIncome = transactions
        .filter { it.type == "income" && it.date.startsWith("2026-06") }
        .sumOf { it.amount }

    val netSavings = (thisMonthIncome - thisMonthExpenses).coerceAtLeast(0.0)
    val savingsRate = if (thisMonthIncome > 0) (netSavings / thisMonthIncome) else 0.0

    val healthScore = CoachEngine.calculateFinancialHealth(transactions, goals, profile, challenges)
    val dailyInsight = CoachEngine.generateDailyInsight(transactions, profile)
    val activeChallenge = challenges.firstOrNull { it.status == "active" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
    ) {
        // Welcome and Profile Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good Day,",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${profile.name} 👋",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Coin count
                    Surface(
                        color = AccentYellow.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(end = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = "🪙 ", fontSize = 14.sp)
                            Text(
                                text = "${profile.coins}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Avatar button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(4.dp, CircleShape, clip = false)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PrimaryGreen, SecondaryBlue)
                                )
                            )
                            .clickable { onOpenProfile() }
                            .testTag("profile_avatar_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.name.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }

        // Wallet Balance Quick Stats Card
        item {
            PremiumCard(
                backgroundColor = MaterialTheme.colorScheme.primary,
                elevation = 8.dp
            ) {
                Text(
                    text = "NET MONTHLY SAVINGS",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = netSavings.toNiceString(symbol),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "MONTHLY INCOME",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = thisMonthIncome.toNiceString(symbol),
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "MONTHLY SPENT",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = thisMonthExpenses.toNiceString(symbol),
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(modifier = Modifier.weight(0.8f)) {
                        Text(
                            text = "SAVINGS RATE",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${(savingsRate * 100).toInt()}%",
                            fontSize = 16.sp,
                            color = AccentYellow,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // Quick Actions Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onOpenAddTransaction,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("quick_add_transaction_button"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Icon")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Record", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Button(
                    onClick = { viewModel.setTab(2) }, // Go to Coach tab
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("should_i_buy_button"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Should I Buy?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        // AI Health Score & Insight Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Card: Health Score Gauge (blue light bg)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (MaterialTheme.colorScheme.background == SoftCream) BlueLightBg else SlateCard
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(145.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CompactHealthGauge(
                            score = healthScore,
                            size = 64.dp,
                            strokeWidth = 6.dp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "HEALTH SCORE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryBlue,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Right Card: AI Insight (amber light bg)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (MaterialTheme.colorScheme.background == SoftCream) AmberLightBg else SlateCard
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(145.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "AI INSIGHT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentYellow,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dailyInsight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (MaterialTheme.colorScheme.background == SoftCream) DarkText.copy(alpha = 0.85f) else SlateText,
                            lineHeight = 15.sp,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Today's Saving Micro-Challenge
        if (activeChallenge != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (MaterialTheme.colorScheme.background == SoftCream) PurpleLightBg else SlateCard
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (MaterialTheme.colorScheme.background == SoftCream) Color(0xFFEDE9FE) else SlateBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = "Challenge",
                                        tint = Color(0xFF7C3AED),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "DAILY MICRO-CHALLENGE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF7C3AED),
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = activeChallenge.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Potential Save: $symbol${activeChallenge.amount.toInt()} • +${activeChallenge.rewardCoins} Coins",
                                        fontSize = 12.sp,
                                        color = PrimaryGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.skipChallenge(activeChallenge) },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)),
                                modifier = Modifier.weight(0.8f)
                            ) {
                                Text("Skip", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.completeChallenge(activeChallenge) },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Done", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("I Saved This!", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Dream Board Progress Title
        if (goals.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Dream Boards",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "View All",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { viewModel.setTab(3) } // Go to Goals tab
                    )
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(goals) { goal ->
                        val ratio = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .width(180.dp)
                                .shadow(2.dp, RoundedCornerShape(20.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = goal.emoji,
                                        fontSize = 24.sp
                                    )
                                    Surface(
                                        color = if (goal.priority == "High") AlertRed.copy(alpha = 0.08f) else SecondaryBlue.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = goal.priority,
                                            fontSize = 9.sp,
                                            color = if (goal.priority == "High") AlertRed else SecondaryBlue,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = goal.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${(ratio * 100).toInt()}% Saved",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                PremiumProgressBar(
                                    progress = ratio,
                                    height = 6.dp,
                                    progressColor = if (ratio > 0.7f) PrimaryGreen else SecondaryBlue
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Transactions Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "See All",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { viewModel.setTab(1) } // Go to Expenses tab
                )
            }
        }

        // Recent Transactions List
        val recentTransactions = transactions.take(4)
        if (recentTransactions.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "Receipt",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No records added yet.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        } else {
            items(recentTransactions) { tx ->
                TransactionListItem(
                    transaction = tx,
                    currencySymbol = symbol,
                    onDelete = { viewModel.deleteTransaction(tx) }
                )
            }
        }
    }
}

@Composable
fun TransactionListItem(
    transaction: TransactionEntity,
    currencySymbol: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isExpense = transaction.type == "expense"
    val tint = if (isExpense) AlertRed else PrimaryGreen
    val icon = when (transaction.category) {
        "Food" -> Icons.Default.Restaurant
        "Groceries" -> Icons.Default.ShoppingCart
        "Shopping" -> Icons.Default.ShoppingBag
        "Rent" -> Icons.Default.Home
        "Travel" -> Icons.Default.DirectionsCar
        "Fuel" -> Icons.Default.LocalGasStation
        "Subscriptions" -> Icons.Default.Subscriptions
        "Salary" -> Icons.Default.Paid
        "Freelance" -> Icons.Default.Work
        "Health" -> Icons.Default.MedicalServices
        else -> Icons.Default.ReceiptLong
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
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
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(tint.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = transaction.category,
                        tint = tint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = transaction.merchant.ifEmpty { transaction.category },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = transaction.date,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Medium
                        )
                        if (transaction.needWant.isNotEmpty() && isExpense) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = if (transaction.needWant == "need") PrimaryGreen.copy(alpha = 0.08f) else AccentYellow.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = transaction.needWant.uppercase(),
                                    fontSize = 8.sp,
                                    color = if (transaction.needWant == "need") PrimaryGreen else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${if (isExpense) "-" else "+"}$currencySymbol${transaction.amount.toInt()}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = tint
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
