package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Int)
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoalById(id: Int)
}

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges ORDER BY date DESC, id DESC")
    fun getAllChallenges(): Flow<List<ChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ChallengeEntity)

    @Update
    suspend fun updateChallenge(challenge: ChallengeEntity)

    @Query("DELETE FROM challenges WHERE id = :id")
    suspend fun deleteChallengeById(id: Int)
}

@Dao
interface PurchaseAdviceDao {
    @Query("SELECT * FROM purchase_advices ORDER BY createdAt DESC")
    fun getAllAdvices(): Flow<List<PurchaseAdviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdvice(advice: PurchaseAdviceEntity)

    @Delete
    suspend fun deleteAdvice(advice: PurchaseAdviceEntity)
}

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile WHERE id = 1")
    fun getProfile(): Flow<ProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)
}
