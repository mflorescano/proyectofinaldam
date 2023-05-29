package com.mario.citas.ui.viewmodels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mario.citas.core.utils.serializeToMap
import com.mario.citas.ui.local.UserDataBase
import com.mario.citas.ui.models.User
import com.mario.citas.ui.models.toEntity
import com.mario.citas.ui.models.toView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun clear() = _uiState.update { currentState ->
        currentState.copy(
            success = null
        )
    }

    fun registerUser(user: User, password: String, activity: Activity) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(user.email, password)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful && task.result.user != null) {
                        addingUserToFirestore(user, task.result.user!!.uid, activity)
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

    private fun addingUserToFirestore(user: User, userID: String, activity: Activity) {
        val userEntity = user.toEntity()
        firestore.collection("users").document(userID).set(userEntity.serializeToMap())
            .addOnSuccessListener { documentReference ->
                viewModelScope.launch(Dispatchers.IO) {
                    addUserLocally(user, activity)
                }
                _uiState.update { currentState ->
                    currentState.copy(
                        success = userEntity.toView(userID)
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

    private suspend fun addUserLocally(user: User, context: Context) {
        val db = Room.databaseBuilder(
            context,
            UserDataBase::class.java, "user-database"
        ).build()
        db.userDao().insert(user.toEntity())
    }

    data class RegisterUiState(
        val success: User? = null,
        val error: String? = null,
    )
}