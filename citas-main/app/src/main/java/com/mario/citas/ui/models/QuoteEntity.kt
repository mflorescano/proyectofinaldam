package com.mario.citas.ui.models

data class QuoteEntity(
    val quoteStart : CalendarEntity? = null,
    val quoteEnd: CalendarEntity? = null,
    val inNameOf: String? = null,
    val emailOf: String? = null
)
