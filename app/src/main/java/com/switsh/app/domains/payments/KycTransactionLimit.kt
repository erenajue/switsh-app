package com.switsh.app.domains.payments

import java.math.BigDecimal

enum class TransactionCurrency(val code: String) {
    USD("USD"),
    CDF("CDF"),
}

object KycTransactionLimit {
    val placeholderMonthlyRemainingUsd = BigDecimal("1180.00")
    val placeholderUsdToCdfRate = BigDecimal("2850")

    fun monthlyRemaining(currency: TransactionCurrency): BigDecimal =
        when (currency) {
            TransactionCurrency.USD -> placeholderMonthlyRemainingUsd
            TransactionCurrency.CDF -> placeholderMonthlyRemainingUsd.multiply(placeholderUsdToCdfRate)
        }

    fun isExceeded(amount: BigDecimal, currency: TransactionCurrency): Boolean =
        amount > monthlyRemaining(currency)
}
