package com.mario.citas.ui.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.mario.citas.ui.models.QuoteEntity
import com.mario.citas.ui.models.User
import com.mario.citas.ui.models.UserEntity
import com.mario.citas.ui.models.toView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val firestore = Firebase.firestore

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun clear() = _uiState.update { currentState ->
        currentState.copy(
            success = null
        )
    }

    fun getAllQuotes() {
        firestore.collection("quotes").get()
            .addOnSuccessListener { result ->
                _uiState.update { currentState ->
                    currentState.copy(
                        success = result.map {
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