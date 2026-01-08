package com.example.lojasocial.ui.pedidos

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.Pedido
import com.example.lojasocial.models.PedidoStatus
import com.example.lojasocial.repositories.PedidoRepository
import com.example.lojasocial.repositories.BeneficiarioRepository
import com.example.lojasocial.repositories.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class NovosPedidosState(
    val isLoading: Boolean = true,
    val pedidos: List<Pedido> = emptyList(),
    val beneficiarios: Map<String, String> = emptyMap(),
    val error: String? = null
)

@HiltViewModel
class NovosPedidosListViewModel @Inject constructor(
    private val pedidoRepository: PedidoRepository,
    private val beneficiarioRepository: BeneficiarioRepository
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
                    val nomes = carregarNomesPedidos(pedidos)
                    _uiState.value = NovosPedidosState(
                        isLoading = false,
                        pedidos = pedidos,
                        beneficiarios = nomes
                    )
                }
        }
    }

    private suspend fun carregarNomesPedidos(pedidos: List<Pedido>): Map<String, String> {
        val nomesMap = mutableMapOf<String, String>()
        pedidos.forEach { pedido ->
            val result = beneficiarioRepository.obterBeneficiario(pedido.beneficiarioId)
            nomesMap[pedido.beneficiarioId] = when (result) {
                is ResultWrapper.Success -> result.value.nome.toString()
                else -> "ID: ${pedido.beneficiarioId}"
            }
        }
        return nomesMap
    }

    private suspend fun getNomeBeneficiario(id: String): String {
        return when (val result = beneficiarioRepository.obterBeneficiario(id)) {
            is ResultWrapper.Success -> result.value.nome.toString()
            else -> "ID: $id"
        }
    }

}

