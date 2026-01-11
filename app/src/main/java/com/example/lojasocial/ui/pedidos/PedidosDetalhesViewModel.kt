package com.example.lojasocial.ui.pedidos

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.Pedido
import com.example.lojasocial.repositories.BeneficiarioRepository
import com.example.lojasocial.repositories.PedidoRepository
import com.example.lojasocial.repositories.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.lojasocial.models.Entrega
import com.example.lojasocial.models.EntregaStatus
import com.example.lojasocial.repositories.EntregaRepository

data class PedidoDetalhesState(
    val isLoading: Boolean = true,
    val pedido: Pedido? = null,
    val nomeBeneficiario: String? = "",
    val emailBeneficiario: String? = "", // NOVO CAMPO
    val error: String? = null
)

@HiltViewModel
class PedidoDetalhesViewModel @Inject constructor(
    private val pedidoRepository: PedidoRepository,
    private val beneficiarioRepository: BeneficiarioRepository,
    private val entregaRepository: EntregaRepository
) : ViewModel() {

    private val _uiState = mutableStateOf(PedidoDetalhesState())
    val uiState: State<PedidoDetalhesState> = _uiState

    fun carregarPedido(pedidoId: String) {
        _uiState.value = PedidoDetalhesState(isLoading = true)

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

                when (val resultado = beneficiarioRepository.obterBeneficiario(pedido.beneficiarioId)) {
                    is ResultWrapper.Success -> {
                        _uiState.value = PedidoDetalhesState(
                            isLoading = false,
                            pedido = pedido,
                            nomeBeneficiario = resultado.value.nome,
                            emailBeneficiario = resultado.value.email // PREENCHER O EMAIL
                        )
                    }
                    is ResultWrapper.Error -> {
                        _uiState.value = PedidoDetalhesState(
                            isLoading = false,
                            pedido = pedido,
                            nomeBeneficiario = "Desconhecido",
                            emailBeneficiario = "",
                            error = resultado.message
                        )
                    }
                    is ResultWrapper.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }

            } catch (e: Exception) {
                _uiState.value = PedidoDetalhesState(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun verificarEaceitar(pedidoId: String, onAvisoEntregaAtiva: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val pedido = _uiState.value.pedido ?: return@launch

            val temAtiva = entregaRepository.temEntregaAtiva(pedido.beneficiarioId)

            if (temAtiva) {
                onAvisoEntregaAtiva()
            } else {
                aceitarPedido(pedidoId, onSuccess)
            }
        }
    }

    fun aceitarPedido(
        pedidoId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val pedido = pedidoRepository.getPedidoById(pedidoId) ?: return@launch

                val novaEntrega = Entrega(
                    beneficiarioId = pedido.beneficiarioId,
                    pedidoId = pedidoId,
                    itens = emptyList(),
                    status = EntregaStatus.EM_ANDAMENTO
                )

                entregaRepository.criarEntrega(novaEntrega)
                pedidoRepository.aceitarPedido(pedidoId)

                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Erro ao aceitar pedido: ${e.message}")
            }
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