package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "expense" or "income"
    val amount: Double,
    val category: String,
    val paymentMethod: String,
    val merchant: String,
    val note: String,
    val date: String, // "YYYY-MM-DD"
    val time: String, // "HH:MM"
    val isRecurring: Boolean,
    val needWant: String, // "need" or "want" or "income"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val emoji: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val targetDate: String, // "YYYY-MM-DD"
    val monthlyContribution: Double,
    val priority: String, // "High", "Medium", "Low"
    val category: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val status: String, // "active", "completed", "skipped"
    val rewardCoins: Int,
    val date: String, // "YYYY-MM-DD"
    val completedAt: Long = 0L
)

@Entity(tableName = "purchase_advices")
data class PurchaseAdviceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemName: String,
    val price: Double,
    val category: String,
    val urgency: String, // "Very Urgent", "Somewhat", "Not Urgent"
    val reason: String,
    val recommendation: String, // "BUY", "WAIT", "AVOID"
    val score: Int, // 0 to 100
    val workHours: Double,
    val goalDelayDays: Int,
    val needWantRatio: Int, // Want score (0-100)
    val planSuggestion: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Natasha",
    val currencySymbol: String = "₹",
    val monthlyIncome: Double = 50000.0,
    val workHoursPerDay: Double = 8.0,
    val monthlySavingsTarget: Double = 15000.0,
    val emergencyFundTarget: Double = 100000.0,
    val theme: String = "Cream",
    val coins: Int = 100
)
