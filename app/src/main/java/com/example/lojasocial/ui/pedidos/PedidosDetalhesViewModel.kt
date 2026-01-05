package com.example.lojasocial.ui.pedidos

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.Pedido
import com.example.lojasocial.repositories.BeneficiarioRepository
import com.example.lojasocial.repositories.PedidoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PedidoDetalhesState(
    val isLoading: Boolean = true,
    val pedido: Pedido? = null,
    val nomeBeneficiario: String = "",
    val error: String? = null
)

@HiltViewModel
class PedidoDetalhesViewModel @Inject constructor(
    private val pedidoRepository: PedidoRepository,
    private val beneficiarioRepository: BeneficiarioRepository
) : ViewModel() {

    private val _uiState = mutableStateOf(PedidoDetalhesState())
    val uiState: State<PedidoDetalhesState> = _uiState

    fun carregarPedido(pedidoId: String) {
        viewModelScope.launch {
            try {
                val pedido = pedidoRepository.getPedidoById(pedidoId)

                if (pedido == null) {
                    _uiState.value = PedidoDetalhesState(
                        isLoading = false,
                        error = "Pedido não encontrado"
                    )
                    return@launch
                }

                val beneficiario = beneficiarioRepository
                    .getBeneficiarioById(pedido.beneficiarioId)

                _uiState.value = PedidoDetalhesState(
                    isLoading = false,
                    pedido = pedido,
                    nomeBeneficiario = beneficiario?.nome ?: "Desconhecido"
                )

            } catch (e: Exception) {
                _uiState.value = PedidoDetalhesState(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun aceitarPedido(pedidoId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            pedidoRepository.aceitarPedido(pedidoId)
            onSuccess()
        }
    }

    fun recusarPedido(
        pedidoId: String,
        motivo: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            pedidoRepository.recusarPedido(pedidoId, motivo)
            onSuccess()
        }
    }
}
