package com.example.lojasocial.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    fun login(email: String, password: String): Flow<ResultWrapper<Boolean>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user

            if (user != null) {
                val doc = db.collection("funcionarios").document(user.uid).get().await()

                if (doc.exists()) {
                    emit(ResultWrapper.Success(true))
                } else {
                    auth.signOut()
                    emit(ResultWrapper.Error(message = "Acesso restrito a funcionários."))
                }
            } else {
                emit(ResultWrapper.Error(message = "Erro desconhecido."))
            }

        } catch (e: Exception) {
            emit(ResultWrapper.Error(message = e.message))
        }
    }

    fun recoverPassword(email: String): Flow<ResultWrapper<Unit>> = flow {
        emit(ResultWrapper.Loading)
        try {
            auth.sendPasswordResetEmail(email).await()
            emit(ResultWrapper.Success(Unit))
        } catch (e: Exception) {
            emit(ResultWrapper.Error(message = e.message))
        }
    }
}