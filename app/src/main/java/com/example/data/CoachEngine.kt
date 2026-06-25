package com.example.data

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object CoachEngine {

    fun calculateWorkHours(price: Double, monthlyIncome: Double, workHoursPerDay: Double): Double {
        if (monthlyIncome <= 0 || workHoursPerDay <= 0) return 0.0
        val workingDays = 22.0
        val totalHours = workingDays * workHoursPerDay
        val hourlyRate = monthlyIncome / totalHours
        return (price / hourlyRate * 10.0).roundToInt() / 10.0
    }

    fun calculateGoalDelayDays(price: Double, monthlySavingsTarget: Double): Int {
        if (monthlySavingsTarget <= 0) return 0
        val dailySavings = monthlySavingsTarget / 30.0
        return (price / dailySavings).roundToInt()
    }

    fun analyzePurchase(
        itemName: String,
        price: Double,
        category: String,
        urgency: String, // "Very Urgent", "Somewhat", "Not Urgent"
        reason: String,
        needWant: String, // "need" or "want"
        profile: ProfileEntity,
        transactions: List<TransactionEntity>
    ): PurchaseAdviceEntity {
        val workHours = calculateWorkHours(price, profile.monthlyIncome, profile.workHoursPerDay)
        val goalDelayDays = calculateGoalDelayDays(price, profile.monthlySavingsTarget)

        // Calculate need vs want score (0-100)
        var wantScore = if (needWant == "want") 75 else 25
        
        // Adjust for emotional keywords
        val emotionalKeywords = listOf("sad", "bored", "depressed", "treat", "impulse", "status", "tired", "peer")
        val isEmotional = emotionalKeywords.any { reason.lowercase().contains(it) }
        if (isEmotional) {
            wantScore += 15
        }
        
        if (urgency == "Not Urgent") {
            wantScore += 10
        } else if (urgency == "Very Urgent") {
            wantScore -= 15
        }
        val needWantRatio = min(100, max(0, wantScore))

        // Affordability Score (0-100)
        var score = 100
        
        // Factor 1: Price relative to monthly income
        val ratio = price / profile.monthlyIncome
        when {
            ratio > 1.0 -> score -= 70
            ratio > 0.5 -> score -= 50
            ratio > 0.2 -> score -= 30
            ratio > 0.05 -> score -= 15
        }

        // Factor 2: Want vs Need ratio
        if (needWantRatio > 50) {
            score -= ((needWantRatio - 50) * 0.6).toInt()
        }

        // Factor 3: Financial health adjustment (based on monthly target)
        // Let's assume average savings rate in transactions
        val thisMonthExpenses = transactions
            .filter { it.type == "expense" && it.date.startsWith("2026-06") } // current month simulation
            .sumOf { it.amount }
        val remainingBudget = profile.monthlyIncome - thisMonthExpenses
        if (remainingBudget < profile.monthlySavingsTarget) {
            score -= 15
        }

        score = min(100, max(1, score))

        // Determine Recommendation
        val recommendation = when {
            price > profile.monthlyIncome * 0.8 && urgency != "Very Urgent" -> "AVOID"
            score >= 75 -> "BUY"
            score in 50..74 -> "WAIT"
            score in 30..49 -> "WAIT"
            else -> "AVOID"
        }

        // Suggest Plan
        val symbol = profile.currencySymbol
        val planSuggestion = when (recommendation) {
            "BUY" -> "Affordable! You can buy this safely. To offset, try saving ${symbol}${(price * 0.1).roundToInt()} extra this week."
            "WAIT" -> {
                val weeklySave = (price / 8.0).roundToInt()
                "Wait 30 Days. Plan: Save ${symbol}${weeklySave}/week for 8 weeks to buy guilt-free without touching emergency funds."
            }
            else -> {
                val cheaperPrice = (price * 0.4).roundToInt()
                "Avoid now. Alternative: Look for refurbished options around ${symbol}${cheaperPrice} or skip completely to secure ${symbol}${price.roundToInt()} in savings!"
            }
        }

        return PurchaseAdviceEntity(
            itemName = itemName,
            price = price,
            category = category,
            urgency = urgency,
            reason = reason,
            recommendation = recommendation,
            score = score,
            workHours = workHours,
            goalDelayDays = goalDelayDays,
            needWantRatio = needWantRatio,
            planSuggestion = planSuggestion
        )
    }

    fun calculateFinancialHealth(
        transactions: List<TransactionEntity>,
        goals: List<GoalEntity>,
        profile: ProfileEntity,
        challenges: List<ChallengeEntity>
    ): Int {
        var score = 50

        // 1. Savings Rate component (up to 20 pts)
        val income = transactions.filter { it.type == "income" }.sumOf { it.amount }
        val expense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
        val activeIncome = if (income > 0) income else profile.monthlyIncome
        val actualSavings = max(0.0, activeIncome - expense)
        val savingsRate = if (activeIncome > 0) actualSavings / activeIncome else 0.0
        
        score += when {
            savingsRate >= 0.3 -> 20
            savingsRate >= 0.15 -> 15
            savingsRate >= 0.01 -> 8
            else -> 0
        }

        // 2. Goal Progress component (up to 15 pts)
        if (goals.isNotEmpty()) {
            val avgProgress = goals.map { it.currentAmount / max(1.0, it.targetAmount) }.average()
            score += (avgProgress * 15).roundToInt()
        } else {
            score += 10
        }

        // 3. Challenge Completion component (up to 15 pts)
        if (challenges.isNotEmpty()) {
            val completed = challenges.count { it.status == "completed" }
            val rate = completed.toDouble() / challenges.size
            score += (rate * 15).roundToInt()
        } else {
            score += 10
        }

        // 4. Expense Control (up to 15 pts)
        val expenseRatio = if (activeIncome > 0) expense / activeIncome else 0.5
        score += when {
            expenseRatio < 0.6 -> 15
            expenseRatio < 0.8 -> 10
            expenseRatio < 0.95 -> 5
            else -> 0
        }

        return min(100, max(5, score))
    }

    fun generateDailyInsight(transactions: List<TransactionEntity>, profile: ProfileEntity): String {
        val expense = transactions.filter { it.type == "expense" }
        val totalExpense = expense.sumOf { it.amount }
        val symbol = profile.currencySymbol

        if (totalExpense <= 0) {
            return "Welcome! Add your first transactions to receive personal, AI-powered wealth coaching."
        }

        val categoryGroups = expense.groupBy { it.category }
        val highestCategory = categoryGroups.maxByOrNull { it.value.sumOf { e -> e.amount } }

        return when ((System.currentTimeMillis() % 5).toInt()) {
            0 -> "Insight: You spent the most on ${highestCategory?.key ?: "Shopping"}. Cutting this by 15% would add ${symbol}${(profile.monthlyIncome * 0.05).roundToInt()} to your savings goal."
            1 -> "Tip: Wait 48 hours before checking out online impulse carts. It decreases digital shopping waste by 65%!"
            2 -> "Insight: Your monthly savings target is ${symbol}${profile.monthlySavingsTarget.roundToInt()}. Small daily wins of ${symbol}200 can easily bridge any shortfalls!"
            3 -> "Mindset: A purchase is never just money; it's also life-hours. Always compute the life-hours cost before buying luxury."
            else -> "Challenge: Complete today's saving micro-challenge to boost your financial health score and earn XP rewards!"
        }
    }

    val presetChallenges = listOf(
        "No Food Delivery Today" to 150.0,
        "Skip Coffee Shop Purchase" to 80.0,
        "No Online Shopping Today" to 200.0,
        "Cook at Home" to 120.0,
        "Use Public Transport" to 50.0,
        "Avoid Impulse Grocery Item" to 60.0,
        "Save Your Spare Change" to 30.0,
        "No Subscriptions Used Day" to 40.0
    )

    fun generateSavingChallenge(date: String): ChallengeEntity {
        // Deterministically pick a preset challenge based on date hash
        val index = max(0, date.hashCode() % presetChallenges.size)
        val (title, amount) = presetChallenges[index]
        val reward = (amount * 0.2).roundToInt().coerceAtLeast(10)
        return ChallengeEntity(
            title = title,
            amount = amount,
            status = "active",
            rewardCoins = reward,
            date = date
        )
    }
}
