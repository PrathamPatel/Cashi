package com.cashi.technical.routes.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val error: String)