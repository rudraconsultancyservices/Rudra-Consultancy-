package com.example.data

import kotlinx.coroutines.flow.firstOrNull

object SampleData {

    suspend fun populateSampleDataIfEmpty(repository: MoneyRepository) {
        val currentProfile = repository.profile.firstOrNull()
        if (currentProfile != null) return // Already populated or has data

        // Insert default profile
        val defaultProfile = ProfileEntity(
            id = 1,
            name = "Natasha",
            currencySymbol = "₹",
            monthlyIncome = 65000.0,
            workHoursPerDay = 8.0,
            monthlySavingsTarget = 18000.0,
            emergencyFundTarget = 120000.0,
            theme = "Cream",
            coins = 150
        )
        repository.updateProfile(defaultProfile)

        // Insert sample goals
        val travelGoal = GoalEntity(
            name = "Trip to Japan",
            emoji = "✈️",
            targetAmount = 150000.0,
            currentAmount = 45000.0,
            targetDate = "2027-04-12",
            monthlyContribution = 5000.0,
            priority = "High",
            category = "Travel"
        )
        val emergencyGoal = GoalEntity(
            name = "Emergency Fund",
            emoji = "🛡️",
            targetAmount = 100000.0,
            currentAmount = 60000.0,
            targetDate = "2026-12-31",
            monthlyContribution = 8000.0,
            priority = "High",
            category = "Investment"
        )
        val laptopGoal = GoalEntity(
            name = "New Pro Laptop",
            emoji = "💻",
            targetAmount = 85000.0,
            currentAmount = 30000.0,
            targetDate = "2026-10-15",
            monthlyContribution = 4500.0,
            priority = "Medium",
            category = "Education"
        )
        repository.insertGoal(travelGoal)
        repository.insertGoal(emergencyGoal)
        repository.insertGoal(laptopGoal)

        // Insert sample challenges
        val challenge1 = ChallengeEntity(
            title = "No Food Delivery Today",
            amount = 150.0,
            status = "completed",
            rewardCoins = 30,
            date = "2026-06-24",
            completedAt = System.currentTimeMillis() - 86400000L
        )
        val challenge2 = ChallengeEntity(
            title = "Skip Coffee Shop Purchase",
            amount = 80.0,
            status = "completed",
            rewardCoins = 15,
            date = "2026-06-23",
            completedAt = System.currentTimeMillis() - 172800000L
        )
        val challenge3 = ChallengeEntity(
            title = "No Online Shopping Today",
            amount = 200.0,
            status = "active",
            rewardCoins = 40,
            date = "2026-06-25"
        )
        repository.insertChallenge(challenge1)
        repository.insertChallenge(challenge2)
        repository.insertChallenge(challenge3)

        // Insert sample transactions
        val transactions = listOf(
            TransactionEntity(
                type = "income",
                amount = 65000.0,
                category = "Salary",
                paymentMethod = "Bank Transfer",
                merchant = "Stellar Tech Corp",
                note = "Monthly salary package",
                date = "2026-06-01",
                time = "10:00",
                isRecurring = true,
                needWant = "income"
            ),
            TransactionEntity(
                type = "income",
                amount = 12500.0,
                category = "Freelance",
                paymentMethod = "Bank Transfer",
                merchant = "Upwork Client",
                note = "Landing page design",
                date = "2026-06-15",
                time = "14:30",
                isRecurring = false,
                needWant = "income"
            ),
            TransactionEntity(
                type = "expense",
                amount = 18000.0,
                category = "Rent",
                paymentMethod = "Bank Transfer",
                merchant = "Grand View Apartments",
                note = "Monthly Rent",
                date = "2026-06-02",
                time = "09:15",
                isRecurring = true,
                needWant = "need"
            ),
            TransactionEntity(
                type = "expense",
                amount = 3500.0,
                category = "Groceries",
                paymentMethod = "Debit Card",
                merchant = "HyperMart",
                note = "Weekly bulk grocery shopping",
                date = "2026-06-04",
                time = "18:20",
                isRecurring = false,
                needWant = "need"
            ),
            TransactionEntity(
                type = "expense",
                amount = 1200.0,
                category = "Food",
                paymentMethod = "UPI",
                merchant = "Gourmet Bistro",
                note = "Dinner with friends",
                date = "2026-06-07",
                time = "20:45",
                isRecurring = false,
                needWant = "want"
            ),
            TransactionEntity(
                type = "expense",
                amount = 4500.0,
                category = "Shopping",
                paymentMethod = "Credit Card",
                merchant = "Zara Mall",
                note = "Suede Jacket",
                date = "2026-06-10",
                time = "16:00",
                isRecurring = false,
                needWant = "want"
            ),
            TransactionEntity(
                type = "expense",
                amount = 499.0,
                category = "Subscriptions",
                paymentMethod = "Credit Card",
                merchant = "Netflix Inc",
                note = "Premium 4K plan",
                date = "2026-06-05",
                time = "11:00",
                isRecurring = true,
                needWant = "want"
            ),
            TransactionEntity(
                type = "expense",
                amount = 2000.0,
                category = "Health",
                paymentMethod = "UPI",
                merchant = "Iron Gym",
                note = "Monthly membership renew",
                date = "2026-06-03",
                time = "07:00",
                isRecurring = true,
                needWant = "need"
            ),
            TransactionEntity(
                type = "expense",
                amount = 1800.0,
                category = "Fuel",
                paymentMethod = "UPI",
                merchant = "Shell Stations",
                note = "Car tank fill up",
                date = "2026-06-12",
                time = "15:10",
                isRecurring = false,
                needWant = "need"
            ),
            TransactionEntity(
                type = "expense",
                amount = 650.0,
                category = "Travel",
                paymentMethod = "Wallet",
                merchant = "Uber Ride",
                note = "Ride to downtown office",
                date = "2026-06-18",
                time = "08:45",
                isRecurring = false,
                needWant = "want"
            ),
            TransactionEntity(
                type = "expense",
                amount = 1500.0,
                category = "Health",
                paymentMethod = "UPI",
                merchant = "Apex Pharmacy",
                note = "Multivitamins and medicines",
                date = "2026-06-20",
                time = "12:15",
                isRecurring = false,
                needWant = "need"
            ),
            TransactionEntity(
                type = "expense",
                amount = 350.0,
                category = "Food",
                paymentMethod = "UPI",
                merchant = "Starbucks",
                note = "Iced Macchiato & Cookie",
                date = "2026-06-22",
                time = "16:30",
                isRecurring = false,
                needWant = "want"
            )
        )

        for (tx in transactions) {
            repository.insertTransaction(tx)
        }

        // Insert sample purchase advice
        val adviceSample = PurchaseAdviceEntity(
            itemName = "iPhone 15 Pro",
            price = 120000.0,
            category = "Shopping",
            urgency = "Somewhat",
            reason = "Camera upgrade and status boost",
            recommendation = "WAIT",
            score = 48,
            workHours = 40.6,
            goalDelayDays = 200,
            needWantRatio = 75,
            planSuggestion = "Wait 30 Days. Save ₹15,000/month for 8 months to buy guilt-free without touching emergency funds."
        )
        repository.insertAdvice(adviceSample)
    }
}
