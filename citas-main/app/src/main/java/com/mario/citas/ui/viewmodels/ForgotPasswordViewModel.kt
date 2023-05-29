package com.mario.citas.ui.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun recoverPassword(email: String, activity: Activity) {
        viewModelScope.launch {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        _uiState.update { currentState ->
                            currentState.copy(
                                success = task.isSuccessful
                            )
                        }
                    } else {
                        _uiState.update { currentState ->
                            currentState.copy(
                                error = task.exception?.message
                            )
                        }
                    }
                }
        }
    }

    data class ForgotPasswordUiState(
        val success: Boolean? = null,
        val error: String? = null,
    )
}