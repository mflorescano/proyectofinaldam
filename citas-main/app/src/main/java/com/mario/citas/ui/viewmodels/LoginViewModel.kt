package com.mario.citas.ui.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.mario.citas.ui.models.User
import com.mario.citas.ui.models.UserEntity
import com.mario.citas.ui.models.toView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun clear() = _uiState.update { currentState ->
        currentState.copy(
            success = null, error = null
        )
    }

    fun login(email: String, password: String, activity: Activity) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        if (auth.currentUser != null) {
                            getUserFirestore(auth.currentUser!!.uid)
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

    private fun getUserFirestore(userID: String) {
        val documentRef = firestore.collection("users").document(userID)
        documentRef.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject<UserEntity>()
            _uiState.update { currentState ->
                currentState.copy(
                    success = user?.toView(userID)
                )
            }
        }.addOnFailureListener {
            _uiState.update { currentState ->
                currentState.copy(
                    error = it.message
                )
            }
        }
    }

    data class LoginUiState(
        val success: User? = null,
        val error: String? = null,
    )
}