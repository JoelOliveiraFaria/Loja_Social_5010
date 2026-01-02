package com.example.lojasocial.repositories

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val auth : FirebaseAuth
) {
    fun login(email : String, password : String) : Flow<ResultWrapper<Boolean>> = flow {
        emit(ResultWrapper.Loading)
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            emit(ResultWrapper.Success(true))
        } catch (e : Exception) {
            emit(ResultWrapper.Error(message = e.message))
        }
    }
}
