package com.example.ui.sheets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProfileEntity
import com.example.ui.MoneyViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.ArrowDropDown
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.AlertRed
import com.example.ui.theme.PrimaryGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    viewModel: MoneyViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profileOpt by viewModel.profile.collectAsState()
    val profile = profileOpt ?: ProfileEntity()
    val symbol = profile.currencySymbol

    // Input fields
    var txType by remember { mutableStateOf("expense") } // "expense" or "income"
    var amount by remember { mutableStateOf("") }
    var merchant by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("UPI") }
    var needWant by remember { mutableStateOf("want") } // "need" or "want"
    var note by remember { mutableStateOf("") }

    val expenseCategories = listOf("Food", "Groceries", "Shopping", "Bills", "Rent", "Travel", "Fuel", "Health", "Education", "Entertainment", "Gifts", "Subscriptions", "Other")
    val incomeCategories = listOf("Salary", "Business", "Freelance", "Interest", "Dividend", "Rental", "Gift", "Refund", "Other")
    val paymentMethods = listOf("Cash", "UPI", "Debit Card", "Credit Card", "Bank Transfer", "Wallet", "Other")

    // Set default category on type change
    LaunchedEffect(txType) {
        selectedCategory = if (txType == "expense") "Food" else "Salary"
    }

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
                    text = "Add Ledger Entry",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("dismiss_transaction_sheet")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Income / Expense Toggle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { txType = "expense" },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (txType == "expense") AlertRed else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (txType == "expense") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Expense Outflow", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = { txType = "income" },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (txType == "income") PrimaryGreen else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (txType == "income") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Income Inflow", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("How much?") },
                leadingIcon = { Text(symbol, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transaction_amount_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (txType == "expense") AlertRed else PrimaryGreen
                )
            )

            // Merchant / Source Input
            OutlinedTextField(
                value = merchant,
                onValueChange = { merchant = it },
                label = { Text(if (txType == "expense") "Merchant / Payee" else "Inflow Source") },
                placeholder = { Text(if (txType == "expense") "e.g. Starbucks, Amazon, Landlord" else "e.g. Acme Corp, Client") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transaction_merchant_input")
            )

            // Category Dropdown Selection
            var catExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Financial Category") },
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
                    val activeList = if (txType == "expense") expenseCategories else incomeCategories
                    activeList.forEach { cat ->
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

            // Payment Method Select
            var payExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedPaymentMethod,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Payment Method") },
                    trailingIcon = {
                        IconButton(onClick = { payExpanded = true }) {
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = payExpanded,
                    onDismissRequest = { payExpanded = false }
                ) {
                    paymentMethods.forEach { method ->
                        DropdownMenuItem(
                            text = { Text(method) },
                            onClick = {
                                selectedPaymentMethod = method
                                payExpanded = false
                            }
                        )
                    }
                }
            }

            // Want / Need Toggle (only for expenses)
            if (txType == "expense") {
                Text(
                    text = "Is this purchase a Need or a Want?",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { needWant = "need" },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (needWant == "need") PrimaryGreen else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (needWant == "need") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Need (Essential)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { needWant = "want" },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (needWant == "want") AccentYellow else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (needWant == "want") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Want (Discretionary)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Notes input
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Add specific notes (Optional)") },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Submit Button
            val amountNum = amount.toDoubleOrNull() ?: 0.0
            val isValid = amountNum > 0.0 && selectedCategory.isNotEmpty()
            Button(
                onClick = {
                    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    
                    viewModel.addTransaction(
                        type = txType,
                        amount = amountNum,
                        category = selectedCategory,
                        paymentMethod = selectedPaymentMethod,
                        merchant = merchant.ifEmpty { selectedCategory },
                        note = note,
                        date = dateStr,
                        time = timeStr,
                        isRecurring = false,
                        needWant = if (txType == "expense") needWant else "income"
                    )
                    onDismiss()
                },
                enabled = isValid,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("transaction_submit_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (txType == "expense") AlertRed else PrimaryGreen
                )
            ) {
                Text("Log Entry", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
