package com.example.lojasocial.ui.pedidos

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.Pedido
import com.example.lojasocial.models.PedidoStatus
import com.example.lojasocial.repositories.PedidoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NovosPedidosState(
    val isLoading: Boolean = true,
    val pedidos: List<Pedido> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class NovosPedidosListViewModel @Inject constructor(
    private val pedidoRepository: PedidoRepository
) : ViewModel() {

    private val _uiState = mutableStateOf(NovosPedidosState())
    val uiState: State<NovosPedidosState> = _uiState

    init {
        carregarPedidos()
    }

    private fun carregarPedidos() {
        viewModelScope.launch {
            pedidoRepository
                .getPedidosPorStatus(PedidoStatus.NOVO)
                .collect { pedidos ->
                    _uiState.value = NovosPedidosState(
                        isLoading = false,
                        pedidos = pedidos
                    )
                }
        }
    }
}
