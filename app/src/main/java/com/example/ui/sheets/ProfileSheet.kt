package com.example.ui.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProfileEntity
import com.example.ui.MoneyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSheet(
    viewModel: MoneyViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profileOpt by viewModel.profile.collectAsState()
    val profile = profileOpt ?: ProfileEntity()

    // Form states
    var name by remember(profile) { mutableStateOf(profile.name) }
    var income by remember(profile) { mutableStateOf(profile.monthlyIncome.toInt().toString()) }
    var workHours by remember(profile) { mutableStateOf(profile.workHoursPerDay.toInt().toString()) }
    var savingsTarget by remember(profile) { mutableStateOf(profile.monthlySavingsTarget.toInt().toString()) }
    var emergencyTarget by remember(profile) { mutableStateOf(profile.emergencyFundTarget.toInt().toString()) }
    var selectedCurrency by remember(profile) { mutableStateOf(profile.currencySymbol) }
    var selectedTheme by remember(profile) { mutableStateOf(profile.theme) }

    var showResetConfirm by remember { mutableStateOf(false) }

    val currencies = listOf("₹", "$", "€", "£", "$", "¥")

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
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Coach Preferences",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("dismiss_profile_sheet")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            // User name input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your Coach Alias") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_name_input")
            )

            // Income and Hours Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = income,
                    onValueChange = { income = it },
                    label = { Text("Monthly Net Income") },
                    leadingIcon = { Text(selectedCurrency, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1.1f)
                )

                OutlinedTextField(
                    value = workHours,
                    onValueChange = { workHours = it },
                    label = { Text("Daily Work Hrs") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(0.9f)
                )
            }

            // Savings and Emergency Targets Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = savingsTarget,
                    onValueChange = { savingsTarget = it },
                    label = { Text("Savings Target/Mo") },
                    leadingIcon = { Text(selectedCurrency, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = emergencyTarget,
                    onValueChange = { emergencyTarget = it },
                    label = { Text("Emergency Fund") },
                    leadingIcon = { Text(selectedCurrency, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            // Currency selection
            Text(
                text = "Primary Currency Symbol",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                currencies.forEach { cur ->
                    val active = selectedCurrency == cur
                    Surface(
                        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedCurrency = cur }
                    ) {
                        Text(
                            text = cur,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }
            }

            // Theme selection
            Text(
                text = "Coach Canvas Visual Vibe",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { selectedTheme = "Cream" },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTheme == "Cream") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selectedTheme == "Cream") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Cream Light", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = { selectedTheme = "Dark" },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTheme == "Dark") MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selectedTheme == "Dark") MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Slate Dark", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Save preferences button
            val incomeNum = income.toDoubleOrNull() ?: 0.0
            val hoursNum = workHours.toDoubleOrNull() ?: 0.0
            val targetNum = savingsTarget.toDoubleOrNull() ?: 0.0
            val emergencyNum = emergencyTarget.toDoubleOrNull() ?: 0.0

            val isValid = name.isNotEmpty() && incomeNum > 0 && hoursNum > 0 && targetNum > 0 && emergencyNum > 0

            Button(
                onClick = {
                    viewModel.updateProfile(
                        name = name,
                        currencySymbol = selectedCurrency,
                        monthlyIncome = incomeNum,
                        workHoursPerDay = hoursNum,
                        monthlySavingsTarget = targetNum,
                        emergencyFundTarget = emergencyNum,
                        theme = selectedTheme
                    )
                    onDismiss()
                },
                enabled = isValid,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("profile_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Apply Preferences", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            // Hard Reset Database Trigger
            Button(
                onClick = { showResetConfirm = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Clear and Reset Sample Data", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Reset Confirm Alert Dialog
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset Database?", fontWeight = FontWeight.Bold) },
            text = { Text("This will wipe all custom records and seed original default records. Proceed?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllData()
                        showResetConfirm = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Yes, Wipe", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
