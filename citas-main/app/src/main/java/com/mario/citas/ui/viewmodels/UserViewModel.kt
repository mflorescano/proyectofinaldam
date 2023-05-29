package com.mario.citas.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mario.citas.ui.models.QuoteEntity
import com.mario.citas.ui.models.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val firestore = Firebase.firestore

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun clear() = _uiState.update { currentState ->
        currentState.copy(
            success = null
        )
    }

    fun getUserQuotes(email:String) {
        firestore.collection("quotes").get()
            .addOnSuccessListener { result ->
                _uiState.update { currentState ->
                    currentState.copy(
                        success = result.filter {
                            it.toObject(QuoteEntity::class.java).emailOf == email
                        }.map {
                            it.toObject(QuoteEntity::class.java)
                        }
                    )
                }
            }
    }


    data class LoginUiState(
        val success: List<QuoteEntity>? = null,
        val error: String? = null,
    )
}