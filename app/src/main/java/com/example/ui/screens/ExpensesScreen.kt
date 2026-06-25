package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProfileEntity
import com.example.data.TransactionEntity
import com.example.ui.MoneyViewModel
import com.example.ui.PremiumCard
import com.example.ui.toNiceString
import com.example.ui.theme.AlertRed
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    viewModel: MoneyViewModel,
    onOpenAddTransaction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.transactions.collectAsState()
    val profileOpt by viewModel.profile.collectAsState()
    val profile = profileOpt ?: ProfileEntity()
    val symbol = profile.currencySymbol

    // Filter states
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("All") } // "All", "expense", "income"
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedPaymentMethod by remember { mutableStateOf("All") }

    val keyboardController = LocalSoftwareKeyboardController.current

    // Extract categories & methods for filter lists
    val categories = remember(transactions) {
        listOf("All") + transactions.map { it.category }.distinct().sorted()
    }
    val paymentMethods = remember(transactions) {
        listOf("All") + transactions.map { it.paymentMethod }.filter { it.isNotEmpty() }.distinct().sorted()
    }

    // Apply filters
    val filteredTransactions = remember(transactions, searchQuery, selectedType, selectedCategory, selectedPaymentMethod) {
        transactions.filter { tx ->
            val matchesSearch = tx.merchant.contains(searchQuery, ignoreCase = true) || 
                                tx.category.contains(searchQuery, ignoreCase = true) || 
                                tx.note.contains(searchQuery, ignoreCase = true)
            val matchesType = selectedType == "All" || tx.type.lowercase() == selectedType.lowercase()
            val matchesCategory = selectedCategory == "All" || tx.category == selectedCategory
            val matchesPayment = selectedPaymentMethod == "All" || tx.paymentMethod == selectedPaymentMethod
            matchesSearch && matchesType && matchesCategory && matchesPayment
        }
    }

    val totalExpense = filteredTransactions.filter { it.type == "expense" }.sumOf { it.amount }
    val totalIncome = filteredTransactions.filter { it.type == "income" }.sumOf { it.amount }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search merchant, category, note...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .testTag("expense_search_input")
        )

        // Bottom horizontal filter rows
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Type Selector
            item {
                FilterChipGroup(
                    title = "Type: $selectedType",
                    options = listOf("All", "Expense", "Income"),
                    selectedOption = selectedType,
                    onOptionSelected = { selectedType = it }
                )
            }
            
            // Category Selector
            if (categories.size > 2) { // More than just "All"
                item {
                    FilterChipGroup(
                        title = "Category: $selectedCategory",
                        options = categories,
                        selectedOption = selectedCategory,
                        onOptionSelected = { selectedCategory = it }
                    )
                }
            }

            // Payment Selector
            if (paymentMethods.size > 2) {
                item {
                    FilterChipGroup(
                        title = "Payment: $selectedPaymentMethod",
                        options = paymentMethods,
                        selectedOption = selectedPaymentMethod,
                        onOptionSelected = { selectedPaymentMethod = it }
                    )
                }
            }
        }

        // Summary Statistics Card for current filter
        PremiumCard(
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            elevation = 2.dp,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Filtered Expense",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlertRed
                    )
                    Text(
                        text = totalExpense.toNiceString(symbol),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AlertRed
                    )
                }
                
                VerticalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                    modifier = Modifier.height(36.dp).width(1.dp)
                )

                Column {
                    Text(
                        text = "Filtered Income",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                    Text(
                        text = totalIncome.toNiceString(symbol),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryGreen
                    )
                }

                Button(
                    onClick = onOpenAddTransaction,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Transactions List
        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterListOff,
                        contentDescription = "No results",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No records match active filters.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try clearing search or picking other filters.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(filteredTransactions, key = { it.id }) { tx ->
                    TransactionListItem(
                        transaction = tx,
                        currencySymbol = symbol,
                        onDelete = { viewModel.deleteTransaction(tx) }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChipGroup(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Surface(
            color = if (selectedOption != "All") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.clickable { expanded = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedOption != "All") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier.size(16.dp),
                    tint = if (selectedOption != "All") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
