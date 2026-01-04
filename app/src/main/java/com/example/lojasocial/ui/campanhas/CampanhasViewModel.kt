package com.example.lojasocial.ui.campanhas

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.Campanha
import com.example.lojasocial.repositories.CampanhasRepository
import com.example.lojasocial.repositories.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CampanhasState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val campanhas: List<Campanha> = emptyList()
)

data class CampanhaDetalheState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val campanha: Campanha? = null
)

@HiltViewModel
class CampanhasViewModel @Inject constructor(
    private val repo: CampanhasRepository
) : ViewModel() {

    var uiState = mutableStateOf(CampanhasState())
        private set

    private val _detalheState = MutableStateFlow(CampanhaDetalheState())
    val detalheState: StateFlow<CampanhaDetalheState> = _detalheState.asStateFlow()

    init {
        repo.observeCampanhas()
            .onEach { res ->
                when (res) {
                    is ResultWrapper.Loading -> {
                        uiState.value = uiState.value.copy(isLoading = true, error = null)
                    }

                    is ResultWrapper.Success -> {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = null,
                            campanhas = res.value
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

    fun carregarCampanha(id: String) {
        _detalheState.value = CampanhaDetalheState(isLoading = true, error = null, campanha = null)

        viewModelScope.launch {
            when (val r = repo.obterCampanha(id)) {
                is ResultWrapper.Success -> {
                    _detalheState.value = CampanhaDetalheState(
                        isLoading = false,
                        error = null,
                        campanha = r.value
                    )
                }

                is ResultWrapper.Error -> {
                    _detalheState.value = CampanhaDetalheState(
                        isLoading = false,
                        error = r.message,
                        campanha = null
                    )
                }

                is ResultWrapper.Loading -> {
                    // não deve acontecer aqui (obterCampanha é suspend), mas fica seguro
                    _detalheState.value = _detalheState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun criar(c: Campanha, onDone: () -> Unit) {
        viewModelScope.launch {
            when (val r = repo.criarCampanha(c)) {
                is ResultWrapper.Success -> {
                    onDone()
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

    fun atualizar(c: Campanha, onDone: () -> Unit) {
        viewModelScope.launch {
            when (val r = repo.atualizarCampanha(c)) {
                is ResultWrapper.Success -> {
                    // atualizar detalhe local (para UI refletir logo)
                    _detalheState.value = _detalheState.value.copy(campanha = c, error = null, isLoading = false)
                    onDone()
                }
                is ResultWrapper.Error -> {
                    _detalheState.value = _detalheState.value.copy(error = r.message, isLoading = false)
                }
                is ResultWrapper.Loading -> {
                    _detalheState.value = _detalheState.value.copy(isLoading = true, error = null)
                }
            }
        }
    }

    fun eliminar(id: String) {
        viewModelScope.launch {
            when (val r = repo.eliminarCampanha(id)) {
                is ResultWrapper.Success -> {
                    // opcional: limpar detalhe se era o mesmo
                    val atual = _detalheState.value.campanha
                    if (atual != null && atual.id == id) {
                        _detalheState.value = CampanhaDetalheState()
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
