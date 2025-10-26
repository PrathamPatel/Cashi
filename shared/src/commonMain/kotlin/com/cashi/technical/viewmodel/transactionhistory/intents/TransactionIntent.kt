package com.cashi.technical.viewmodel.transactionhistory.intents

/**
Created By: Pratham
 */
sealed class TransactionIntent {
    data object LoadTransactions : TransactionIntent()
}