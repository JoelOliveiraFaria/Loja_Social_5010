package com.example.lojasocial.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.repositories.LoginRepository
import com.example.lojasocial.repositories.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class LoginState(
    val email: String? = null,
    val password: String? = null,
    val error: String? = null,
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val recoverSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    var uiState = mutableStateOf(LoginState())
        private set

    fun setEmail(email: String) {
        uiState.value = uiState.value.copy(email = email)
    }

    fun setPassword(password: String) {
        uiState.value = uiState.value.copy(password = password)
    }

    fun clearLoginSuccess() {
        uiState.value = uiState.value.copy(loginSuccess = false)
    }

    fun clearRecoverSuccess() {
        uiState.value = uiState.value.copy(recoverSuccess = false)
    }

    fun recoverPassword() {
        val email = uiState.value.email

        if (email.isNullOrEmpty()) {
            uiState.value = uiState.value.copy(error = "Escreva o seu email primeiro.")
            return
        }

        loginRepository.recoverPassword(email)
            .onEach { result ->
                when (result) {
                    is ResultWrapper.Loading -> {
                        uiState.value = uiState.value.copy(isLoading = true, error = null)
                    }
                    is ResultWrapper.Success -> {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = null,
                            recoverSuccess = true
                        )
                    }
                    is ResultWrapper.Error -> {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }


    fun login() {
        uiState.value = uiState.value.copy(isLoading = true, error = null, loginSuccess = false)

        if (uiState.value.email.isNullOrEmpty()) {
            uiState.value = uiState.value.copy(
                error = "O email é obrigatório",
                isLoading = false
            )
            return
        }

        if (uiState.value.password.isNullOrEmpty()) {
            uiState.value = uiState.value.copy(
                error = "A password é obrigatória",
                isLoading = false
            )
            return
        }

        loginRepository.login(
            uiState.value.email!!,
            uiState.value.password!!
        ).onEach { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                        loginSuccess = true
                    )
                }

                is ResultWrapper.Loading -> {
                    uiState.value = uiState.value.copy(
                        isLoading = true,
                        error = null
                    )
                }

                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        error = result.message,
                        isLoading = false,
                        loginSuccess = false
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}
