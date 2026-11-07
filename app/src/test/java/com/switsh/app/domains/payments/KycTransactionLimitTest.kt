package com.switsh.app.domains.payments

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class KycTransactionLimitTest {
    @Test
    fun amountAtMonthlyLimitIsAllowed() {
        assertFalse(KycTransactionLimit.isExceeded(BigDecimal("1180"), TransactionCurrency.USD))
    }

    @Test
    fun amountAboveMonthlyLimitIsBlocked() {
        assertTrue(KycTransactionLimit.isExceeded(BigDecimal("1180.01"), TransactionCurrency.USD))
    }

    @Test
    fun cdfLimitUsesPlaceholderExchangeRate() {
        assertFalse(KycTransactionLimit.isExceeded(BigDecimal("3363000"), TransactionCurrency.CDF))
        assertTrue(KycTransactionLimit.isExceeded(BigDecimal("3363000.01"), TransactionCurrency.CDF))
    }
}
