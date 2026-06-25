package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoneyViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = MoneyRepository(db)

    // UI flows from DB
    val transactions: StateFlow<List<TransactionEntity>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<GoalEntity>> = repository.goals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val challenges: StateFlow<List<ChallengeEntity>> = repository.challenges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val advices: StateFlow<List<PurchaseAdviceEntity>> = repository.advices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profile: StateFlow<ProfileEntity?> = repository.profile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // UI Navigation State
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // AI Advisor State
    private val _coachResult = MutableStateFlow<PurchaseAdviceEntity?>(null)
    val coachResult: StateFlow<PurchaseAdviceEntity?> = _coachResult.asStateFlow()

    private val _isAnalyzingCoach = MutableStateFlow(false)
    val isAnalyzingCoach: StateFlow<Boolean> = _isAnalyzingCoach.asStateFlow()

    init {
        viewModelScope.launch {
            // Populate sample data on first launch if database is empty
            SampleData.populateSampleDataIfEmpty(repository)
            
            // Generate a fresh challenge if today's is missing
            generateDailyChallengeIfNeeded()
        }
    }

    private suspend fun generateDailyChallengeIfNeeded() {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val list = repository.challenges.firstOrNull() ?: emptyList()
        val hasChallengeForToday = list.any { it.date == todayStr }
        if (!hasChallengeForToday) {
            val challenge = CoachEngine.generateSavingChallenge(todayStr)
            repository.insertChallenge(challenge)
        }
    }

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun addTransaction(
        type: String,
        amount: Double,
        category: String,
        paymentMethod: String,
        merchant: String,
        note: String,
        date: String,
        time: String,
        isRecurring: Boolean,
        needWant: String
    ) {
        viewModelScope.launch {
            val tx = TransactionEntity(
                type = type,
                amount = amount,
                category = category,
                paymentMethod = paymentMethod,
                merchant = merchant,
                note = note,
                date = date,
                time = time,
                isRecurring = isRecurring,
                needWant = needWant
            )
            repository.insertTransaction(tx)
        }
    }

    fun deleteTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(tx)
        }
    }

    fun addGoal(
        name: String,
        emoji: String,
        targetAmount: Double,
        currentAmount: Double,
        targetDate: String,
        monthlyContribution: Double,
        priority: String,
        category: String
    ) {
        viewModelScope.launch {
            val goal = GoalEntity(
                name = name,
                emoji = emoji,
                targetAmount = targetAmount,
                currentAmount = currentAmount,
                targetDate = targetDate,
                monthlyContribution = monthlyContribution,
                priority = priority,
                category = category
            )
            repository.insertGoal(goal)
        }
    }

    fun addMoneyToGoal(goal: GoalEntity, amount: Double) {
        viewModelScope.launch {
            val updated = goal.copy(currentAmount = goal.currentAmount + amount)
            repository.insertGoal(updated)
            
            // Also deduct it from cash/bank by logging an investment transaction
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val tx = TransactionEntity(
                type = "expense",
                amount = amount,
                category = "Investment",
                paymentMethod = "UPI",
                merchant = "Allocated to: ${goal.name}",
                note = "Allocated to savings goal: ${goal.name}",
                date = dateStr,
                time = timeStr,
                isRecurring = false,
                needWant = "need"
            )
            repository.insertTransaction(tx)
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    fun completeChallenge(challenge: ChallengeEntity) {
        viewModelScope.launch {
            val updated = challenge.copy(
                status = "completed",
                completedAt = System.currentTimeMillis()
            )
            repository.updateChallenge(updated)

            // Give coin reward
            val prof = profile.value
            if (prof != null) {
                repository.updateProfile(prof.copy(coins = prof.coins + challenge.rewardCoins))
            }

            // Record virtual challenge savings to transaction log
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val tx = TransactionEntity(
                type = "income",
                amount = challenge.amount,
                category = "Other",
                paymentMethod = "Cash",
                merchant = "Savings Challenge Reward",
                note = "Saved by completing challenge: ${challenge.title}",
                date = dateStr,
                time = timeStr,
                isRecurring = false,
                needWant = "income"
            )
            repository.insertTransaction(tx)
        }
    }

    fun skipChallenge(challenge: ChallengeEntity) {
        viewModelScope.launch {
            val updated = challenge.copy(status = "skipped")
            repository.updateChallenge(updated)
        }
    }

    fun analyzePurchase(
        itemName: String,
        price: Double,
        category: String,
        urgency: String,
        reason: String,
        needWant: String
    ) {
        viewModelScope.launch {
            _isAnalyzingCoach.value = true
            // Smooth micro-animation latency for realistic thinking visuals
            kotlinx.coroutines.delay(1200)

            val currentProfile = profile.value ?: ProfileEntity()
            val currentTxList = transactions.value

            val advice = CoachEngine.analyzePurchase(
                itemName = itemName,
                price = price,
                category = category,
                urgency = urgency,
                reason = reason,
                needWant = needWant,
                profile = currentProfile,
                transactions = currentTxList
            )

            repository.insertAdvice(advice)
            _coachResult.value = advice
            _isAnalyzingCoach.value = false
        }
    }

    fun clearCoachResult() {
        _coachResult.value = null
    }

    fun updateProfile(
        name: String,
        currencySymbol: String,
        monthlyIncome: Double,
        workHoursPerDay: Double,
        monthlySavingsTarget: Double,
        emergencyFundTarget: Double,
        theme: String
    ) {
        viewModelScope.launch {
            val current = profile.value ?: ProfileEntity()
            val updated = current.copy(
                name = name,
                currencySymbol = currencySymbol,
                monthlyIncome = monthlyIncome,
                workHoursPerDay = workHoursPerDay,
                monthlySavingsTarget = monthlySavingsTarget,
                emergencyFundTarget = emergencyFundTarget,
                theme = theme
            )
            repository.updateProfile(updated)
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            val dbInstance = AppDatabase.getDatabase(getApplication())
            dbInstance.clearAllTables()
            _coachResult.value = null
            // Re-seed defaults
            SampleData.populateSampleDataIfEmpty(repository)
        }
    }
}
