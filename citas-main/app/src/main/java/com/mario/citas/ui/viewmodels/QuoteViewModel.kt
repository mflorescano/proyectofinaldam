package com.mario.citas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mario.citas.ui.models.QuoteEntity
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuoteViewModel : ViewModel() {

    private val firestore = Firebase.firestore

    private val _uiState = MutableStateFlow(QuoteUiState())
    val uiState: StateFlow<QuoteUiState> = _uiState.asStateFlow()

    fun clear() = _uiState.update { currentState ->
        currentState.copy(
            success = null
        )
    }

    fun registerQuote(quoteEntity: QuoteEntity) {
        viewModelScope.launch {
            checkQuote(quoteEntity)
        }
    }

    private fun checkQuote(quoteEntity: QuoteEntity) {
        firestore.collection("quotes").get()
            .addOnSuccessListener { result ->
                var quoteBusy = false
                result.forEach {
                    val quoteEntityFromService = it.toObject(QuoteEntity::class.java)
                    if (quoteEntityFromService.quoteStart != null &&
                        quoteEntityFromService.quoteEnd != null &&
                        quoteEntity.quoteStart != null &&
                        quoteEntity.quoteEnd != null
                    ) {
                        var startTime = Calendar.getInstance().apply {
                            set(Calendar.DAY_OF_YEAR, quoteEntityFromService.quoteStart.day!!)
                            set(Calendar.MONTH, quoteEntityFromService.quoteStart.month!!)
                            set(Calendar.YEAR, quoteEntityFromService.quoteStart.year!!)
                            set(Calendar.HOUR_OF_DAY, quoteEntityFromService.quoteStart.hour!!)
                            set(Calendar.MINUTE, quoteEntityFromService.quoteStart.minute!!)
                        }

                        var endTime = Calendar.getInstance().apply {
                            set(Calendar.DAY_OF_YEAR, quoteEntityFromService.quoteEnd.day!!)
                            set(Calendar.MONTH, quoteEntityFromService.quoteEnd.month!!)
                            set(Calendar.YEAR, quoteEntityFromService.quoteEnd.year!!)
                            set(Calendar.HOUR_OF_DAY, quoteEntityFromService.quoteEnd.hour!!)
                            set(Calendar.MINUTE, quoteEntityFromService.quoteEnd.minute!!)
                        }

                        var actualTime = Calendar.getInstance().apply {
                            set(Calendar.DAY_OF_YEAR, quoteEntity.quoteStart.day!!)
                            set(Calendar.MONTH, quoteEntity.quoteStart.month!!)
                            set(Calendar.YEAR, quoteEntity.quoteStart.year!!)
                            set(Calendar.HOUR_OF_DAY, quoteEntity.quoteStart.hour!!)
                            set(Calendar.MINUTE, quoteEntity.quoteStart.minute!!)
                        }

                        if (actualTime.after(startTime) && actualTime.before(endTime)) {
                            quoteBusy = true
                        }

                    }
                }
                if (!quoteBusy) {
                    addQuote(quoteEntity)
                } else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            error = "La hora esta ocupada, seleccione otra"
                        )
                    }
                }
            }
            .addOnFailureListener {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = it.message
                    )
                }
            }
    }

    private fun addQuote(quoteEntity: QuoteEntity) {
        firestore.collection("quotes").document().set(quoteEntity)
            .addOnSuccessListener { documentReference ->
                _uiState.update { currentState ->
                    currentState.copy(
                        success = Unit
                    )
                }
            }
            .addOnFailureListener { e ->
                _uiState.update { currentState ->
                    currentState.copy(
                        error = e.message
                    )
                }
            }
    }

    data class QuoteUiState(
        val success: Unit? = null,
        val error: String? = null,
    )
}