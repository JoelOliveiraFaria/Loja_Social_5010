package com.example.lojasocial.ui.beneficiario

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.Beneficiario
import com.example.lojasocial.repositories.BeneficiarioRepository
import com.example.lojasocial.repositories.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BeneficiarioState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val beneficiarios: List<Beneficiario> = emptyList()
)

data class BeneficiarioDetalheState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val beneficiario: Beneficiario? = null
)

@HiltViewModel
class BeneficiarioViewModel @Inject constructor(
    private val repo: BeneficiarioRepository
) : ViewModel() {

    var uiState = mutableStateOf(BeneficiarioState())
        private set

    private val _detalheState = MutableStateFlow(BeneficiarioDetalheState())
    val detalheState: StateFlow<BeneficiarioDetalheState> = _detalheState.asStateFlow()

    init {
        repo.observeBeneficiarios()
            .onEach { res ->
                when (res) {
                    is ResultWrapper.Loading -> {
                        uiState.value = uiState.value.copy(isLoading = true, error = null)
                    }
                    is ResultWrapper.Success -> {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = null,
                            beneficiarios = res.value
                        )
                    }
                    is ResultWrapper.Error -> {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = res.message
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun carregarBeneficiario(id: String) {
        _detalheState.value = BeneficiarioDetalheState(isLoading = true, error = null, beneficiario = null)

        viewModelScope.launch {
            when (val r = repo.obterBeneficiario(id)) {
                is ResultWrapper.Success -> {
                    _detalheState.value = BeneficiarioDetalheState(
                        isLoading = false,
                        error = null,
                        beneficiario = r.value
                    )
                }
                is ResultWrapper.Error -> {
                    _detalheState.value = BeneficiarioDetalheState(
                        isLoading = false,
                        error = r.message,
                        beneficiario = null
                    )
                }
                is ResultWrapper.Loading -> {
                    _detalheState.value = _detalheState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun criar(b: Beneficiario, onDone: () -> Unit) {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true) // Feedback visual na lista/form
            when (val r = repo.criarBeneficiario(b)) {
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(isLoading = false)
                    onDone()
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(isLoading = false, error = r.message)
                }
                is ResultWrapper.Loading -> {
                    uiState.value = uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun atualizar(b: Beneficiario, onDone: () -> Unit) {
        viewModelScope.launch {
            _detalheState.value = _detalheState.value.copy(isLoading = true)
            when (val r = repo.atualizarBeneficiario(b)) {
                is ResultWrapper.Success -> {
                    // Atualiza o estado local do detalhe e finaliza
                    _detalheState.value = _detalheState.value.copy(isLoading = false, beneficiario = b, error = null)
                    onDone()
                }
                is ResultWrapper.Error -> {
                    _detalheState.value = _detalheState.value.copy(isLoading = false, error = r.message)
                }
                is ResultWrapper.Loading -> {
                    _detalheState.value = _detalheState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun eliminar(id: String) {
        viewModelScope.launch {
            when (val r = repo.eliminarBeneficiario(id)) {
                is ResultWrapper.Success -> {
                    val atual = _detalheState.value.beneficiario
                    if (atual != null && atual.id == id) {
                        _detalheState.value = BeneficiarioDetalheState()
                    }
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(error = r.message)
                }
                is ResultWrapper.Loading -> {
                    uiState.value = uiState.value.copy(isLoading = true)
                }
            }
        }
    }
}