package com.example.ui.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProfileEntity
import com.example.ui.MoneyViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalSheet(
    viewModel: MoneyViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profileOpt by viewModel.profile.collectAsState()
    val profile = profileOpt ?: ProfileEntity()
    val symbol = profile.currencySymbol

    // Input states
    var name by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("🎯") }
    var targetMonths by remember { mutableStateOf("12") }
    var selectedPriority by remember { mutableStateOf("High") }
    var selectedCategory by remember { mutableStateOf("Investment") }

    val emojis = listOf("🎯", "✈️", "🛡️", "💻", "🚗", "🏠", "🎓", "💼", "💍", "🎨", "🏖️", "🚴")
    val priorities = listOf("High", "Medium", "Low")
    val categories = listOf("Investment", "Travel", "Education", "Electronics", "Home", "Wedding", "Business", "Other")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New Dream Board",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("dismiss_goal_sheet")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Goal name input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("What are you saving for?") },
                placeholder = { Text("e.g. Trip to Tokyo, New MacBook Pro, Emergency Cash") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("goal_name_input")
            )

            // Target amount & emoji picker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji picker dropdown
                var emojiExpanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(0.6f)) {
                    OutlinedTextField(
                        value = selectedEmoji,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Icon") },
                        trailingIcon = {
                            Text(
                                text = "▼",
                                fontSize = 10.sp,
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .clickable { emojiExpanded = true }
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = emojiExpanded,
                        onDismissRequest = { emojiExpanded = false }
                    ) {
                        emojis.forEach { emo ->
                            DropdownMenuItem(
                                text = { Text(emo, fontSize = 18.sp) },
                                onClick = {
                                    selectedEmoji = emo
                                    emojiExpanded = false
                                }
                            )
                        }
                    }
                }

                // Target Amount input
                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = { targetAmount = it },
                    label = { Text("Target budget") },
                    leadingIcon = { Text(symbol, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1.4f)
                        .testTag("goal_amount_input")
                )
            }

            // Month duration timeline
            OutlinedTextField(
                value = targetMonths,
                onValueChange = { targetMonths = it },
                label = { Text("Timeline in months") },
                placeholder = { Text("e.g. 6, 12, 24") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Category select Dropdown
            var catExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = {
                        IconButton(onClick = { catExpanded = true }) {
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = catExpanded,
                    onDismissRequest = { catExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                selectedCategory = cat
                                catExpanded = false
                            }
                        )
                    }
                }
            }

            // Priority select
            Text(
                text = "Target Savings Priority",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                priorities.forEach { prio ->
                    val active = selectedPriority == prio
                    val color = when (prio) {
                        "High" -> MaterialTheme.colorScheme.error
                        "Medium" -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.primary
                    }

                    Button(
                        onClick = { selectedPriority = prio },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (active) color else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(prio, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Suggested Monthly savings preview
            val targetNum = targetAmount.toDoubleOrNull() ?: 0.0
            val monthsNum = targetMonths.toDoubleOrNull() ?: 12.0
            val suggestion = if (targetNum > 0 && monthsNum > 0) (targetNum / monthsNum).toInt() else 0

            if (suggestion > 0) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💡", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Save $symbol$suggestion / month for ${monthsNum.toInt()} months to complete this goal safely.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Create Button
            val isFormValid = name.isNotEmpty() && targetNum > 0.0 && monthsNum > 0.0
            Button(
                onClick = {
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.MONTH, monthsNum.toInt())
                    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

                    viewModel.addGoal(
                        name = name,
                        emoji = selectedEmoji,
                        targetAmount = targetNum,
                        currentAmount = 0.0,
                        targetDate = dateStr,
                        monthlyContribution = targetNum / monthsNum,
                        priority = selectedPriority,
                        category = selectedCategory
                    )
                    onDismiss()
                },
                enabled = isFormValid,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("goal_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Lock in Dream", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
