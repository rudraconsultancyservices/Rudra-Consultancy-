package com.example.data

import kotlinx.coroutines.flow.Flow

class MoneyRepository(private val db: AppDatabase) {
    val transactions: Flow<List<TransactionEntity>> = db.transactionDao().getAllTransactions()
    val goals: Flow<List<GoalEntity>> = db.goalDao().getAllGoals()
    val challenges: Flow<List<ChallengeEntity>> = db.challengeDao().getAllChallenges()
    val advices: Flow<List<PurchaseAdviceEntity>> = db.purchaseAdviceDao().getAllAdvices()
    val profile: Flow<ProfileEntity?> = db.profileDao().getProfile()

    suspend fun insertTransaction(transaction: TransactionEntity) = db.transactionDao().insertTransaction(transaction)
    suspend fun deleteTransaction(transaction: TransactionEntity) = db.transactionDao().deleteTransaction(transaction)
    suspend fun deleteTransactionById(id: Int) = db.transactionDao().deleteTransactionById(id)

    suspend fun insertGoal(goal: GoalEntity) = db.goalDao().insertGoal(goal)
    suspend fun deleteGoal(goal: GoalEntity) = db.goalDao().deleteGoal(goal)
    suspend fun deleteGoalById(id: Int) = db.goalDao().deleteGoalById(id)

    suspend fun insertChallenge(challenge: ChallengeEntity) = db.challengeDao().insertChallenge(challenge)
    suspend fun updateChallenge(challenge: ChallengeEntity) = db.challengeDao().updateChallenge(challenge)
    suspend fun deleteChallengeById(id: Int) = db.challengeDao().deleteChallengeById(id)

    suspend fun insertAdvice(advice: PurchaseAdviceEntity) = db.purchaseAdviceDao().insertAdvice(advice)
    suspend fun deleteAdvice(advice: PurchaseAdviceEntity) = db.purchaseAdviceDao().deleteAdvice(advice)

    suspend fun updateProfile(profile: ProfileEntity) = db.profileDao().insertProfile(profile)
}
