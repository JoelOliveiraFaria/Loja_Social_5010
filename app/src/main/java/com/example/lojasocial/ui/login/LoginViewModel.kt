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
    var email : String? = null,
    var password : String? = null,
    var error : String? = null,
    var isLoading : Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    val loginRepository: LoginRepository
) : ViewModel() {
    var uiState = mutableStateOf(LoginState())
        private set

    fun setEmail(email: String) {
        uiState.value = uiState.value.copy(email = email)
    }

    fun setPassword(password: String) {
        uiState.value = uiState.value.copy(password = password)
    }

    fun login(onLoginSucess: () -> Unit){
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        if(uiState.value.email.isNullOrEmpty()) {
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

            when(result) {
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                    onLoginSucess(
                    )
                }
                is ResultWrapper.Loading -> {
                    uiState.value = uiState.value.copy(
                        isLoading = true
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}
