package com.switsh.app.screens

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.switsh.app.model.Block
import com.switsh.app.model.ScreenLabels
import com.switsh.app.domains.payments.KycTransactionLimit
import com.switsh.app.domains.payments.TransactionCurrency
import com.switsh.app.ui.ScreenState
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

/** Payment screens currently use prototype data; this applies the demo KYC-limit guard to P2P. */
class PaymentsFragment : ScreenFragment() {
    override fun primary(button: Block, state: ScreenState, onComplete: (Boolean) -> Unit) {
        if (screenId != "pay-p2p") {
            super.primary(button, state, onComplete)
            return
        }

        val amount = state.inputValues["p2pAmount"]?.trim()?.replace(',', '.')?.toBigDecimalOrNull()
        val currencyIndex = state.inputValues["p2pCurrency"]?.toIntOrNull()
        val currency = when (currencyIndex) {
            0 -> TransactionCurrency.USD
            1 -> TransactionCurrency.CDF
            else -> null
        }
        if (amount == null || currency == null || !KycTransactionLimit.isExceeded(amount, currency)) {
            super.primary(button, state, onComplete)
            return
        }

        showKycLimitDialog(amount, currency)
        onComplete(false)
    }

    private fun showKycLimitDialog(amount: BigDecimal, currency: TransactionCurrency) {
        val language = currentLanguage()
        val amountText = formatAmount(amount, currency)
        val limitText = formatAmount(KycTransactionLimit.monthlyRemaining(currency), currency)
        val message = ScreenLabels.resolve("{{payments.kyc_limit.exceeded.message}}", language)
            .orEmpty()
            .format(Locale.ROOT, amountText, limitText)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(ScreenLabels.resolve("{{payments.kyc_limit.exceeded.title}}", language))
            .setMessage(message)
            .setPositiveButton(ScreenLabels.resolve("{{payments.kyc_limit.exceeded.verify}}", language)) { _, _ ->
                navigator.open("onb-status")
            }
            .setNegativeButton(ScreenLabels.resolve("{{common.cancel}}", language), null)
            .show()
    }

    private fun formatAmount(amount: BigDecimal, currency: TransactionCurrency): String {
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            maximumFractionDigits = if (currency == TransactionCurrency.USD) 2 else 0
            minimumFractionDigits = 0
        }
        return "${currency.code} ${formatter.format(amount)}"
    }
}
