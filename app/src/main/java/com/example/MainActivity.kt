package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProfileEntity
import com.example.ui.MoneyViewModel
import com.example.ui.screens.*
import com.example.ui.sheets.AddGoalSheet
import com.example.ui.sheets.AddTransactionSheet
import com.example.ui.sheets.ProfileSheet
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MoneyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val profileOpt by viewModel.profile.collectAsState()
            val profile = profileOpt ?: ProfileEntity()
            
            val isDarkTheme = profile.theme == "Dark"

            MyApplicationTheme(darkTheme = isDarkTheme) {
                val currentTab by viewModel.currentTab.collectAsState()

                // Overlay sheet managers
                var showAddTransaction by remember { mutableStateOf(false) }
                var showAddGoal by remember { mutableStateOf(false) }
                var showProfile by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    Scaffold(
                        bottomBar = {
                            PremiumBottomBar(
                                currentTab = currentTab,
                                onTabSelected = { viewModel.setTab(it) }
                            )
                        },
                        containerColor = Color.Transparent,
                        contentWindowInsets = WindowInsets.navigationBars
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            // Page Crossfades
                            AnimatedContent(
                                targetState = currentTab,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(220)) togetherWith
                                            fadeOut(animationSpec = tween(220))
                                },
                                label = "ScreenTransition"
                            ) { tab ->
                                when (tab) {
                                    0 -> HomeScreen(
                                        viewModel = viewModel,
                                        onOpenAddTransaction = { showAddTransaction = true },
                                        onOpenProfile = { showProfile = true }
                                    )
                                    1 -> ExpensesScreen(
                                        viewModel = viewModel,
                                        onOpenAddTransaction = { showAddTransaction = true }
                                    )
                                    2 -> CoachScreen(
                                        viewModel = viewModel
                                    )
                                    3 -> GoalsScreen(
                                        viewModel = viewModel,
                                        onOpenAddGoal = { showAddGoal = true }
                                    )
                                    4 -> ReportsScreen(
                                        viewModel = viewModel
                                    )
                                }
                            }
                        }
                    }

                    // Sliding Custom Glassmorphic Sheets with dark backdrop overlays
                    AnimatedSheetOverlay(
                        visible = showAddTransaction,
                        onDismiss = { showAddTransaction = false }
                    ) {
                        AddTransactionSheet(
                            viewModel = viewModel,
                            onDismiss = { showAddTransaction = false }
                        )
                    }

                    AnimatedSheetOverlay(
                        visible = showAddGoal,
                        onDismiss = { showAddGoal = false }
                    ) {
                        AddGoalSheet(
                            viewModel = viewModel,
                            onDismiss = { showAddGoal = false }
                        )
                    }

                    AnimatedSheetOverlay(
                        visible = showProfile,
                        onDismiss = { showProfile = false }
                    ) {
                        ProfileSheet(
                            viewModel = viewModel,
                            onDismiss = { showProfile = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedSheetOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(250)),
        exit = fadeOut(tween(250))
    ) {
        // Semi-transparent backdrop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.50f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Sheet slide up
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // Consume click to avoid dismissal
                    )
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(300)
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(300)
                    )
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun PremiumBottomBar(
    currentTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                Triple("Home", Icons.Default.Home, 0),
                Triple("Expenses", Icons.Default.ReceiptLong, 1),
                Triple("Coach AI", Icons.Default.AutoAwesome, 2),
                Triple("Goals", Icons.Default.Savings, 3),
                Triple("Reports", Icons.Default.Analytics, 4)
            )

            tabs.forEach { (label, icon, index) ->
                val active = currentTab == index
                val color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelected(index) }
                        )
                        .testTag("nav_tab_$index"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        color = color,
                        fontSize = 11.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
