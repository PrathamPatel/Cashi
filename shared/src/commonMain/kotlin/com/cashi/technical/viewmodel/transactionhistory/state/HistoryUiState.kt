package com.cashi.technical.viewmodel.transactionhistory.state

import com.cashi.technical.model.Payment

/**
Created By: Pratham
 */
data class HistoryUiState(
    val isLoading : Boolean = false,
    val payments : List<Payment> = emptyList(),
    val error : String? = null
)
