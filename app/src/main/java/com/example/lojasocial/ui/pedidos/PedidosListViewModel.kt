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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PedidosTab {
    NOVOS,
    HISTORICO
}

data class PedidosState(
    val isLoading: Boolean = true,
    val pedidos: List<Pedido> = emptyList(),
    val beneficiarios: Map<String, String> = emptyMap(),
    val error: String? = null,
    val currentTab: PedidosTab = PedidosTab.NOVOS
)

@HiltViewModel
class PedidosListViewModel @Inject constructor(
    private val pedidoRepository: PedidoRepository,
    private val beneficiarioRepository: BeneficiarioRepository
) : ViewModel() {

    private val _uiState = mutableStateOf(PedidosState())
    val uiState: State<PedidosState> = _uiState

    private var currentJob: Job? = null

    init {
        mudarTab(PedidosTab.NOVOS)
    }

    fun mudarTab(tab: PedidosTab) {
        currentJob?.cancel()

        _uiState.value = _uiState.value.copy(
            currentTab = tab,
            isLoading = true,
            pedidos = emptyList()
        )

        currentJob = viewModelScope.launch {
            if (tab == PedidosTab.NOVOS) {
                // --- TAB 1: APENAS NOVOS ---
                pedidoRepository.getPedidosPorStatus(PedidoStatus.NOVO)
                    .collect { pedidos ->
                        atualizarEstado(pedidos)
                    }
            } else {
                // --- TAB 2: HISTÓRICO (Decisões: Aceites ou Recusados) ---

                // 1. ACEITES (Em Andamento) -> Consideramos isto um pedido aceite
                val flowAceites = pedidoRepository.getPedidosPorStatus(PedidoStatus.EM_ANDAMENTO)

                // 2. RECUSADOS -> Pedidos rejeitados
                val flowRecusados = pedidoRepository.getPedidosPorStatus(PedidoStatus.RECUSADO)

                // NOTA: Removemos PRONTO e ENTREGUE daqui, pois isso são "Entregas/Logística"

                merge(flowAceites, flowRecusados)
                    .collect { listaParcial ->
                        // Lógica para acumular e atualizar a lista
                        val listaAtual = _uiState.value.pedidos.toMutableList()

                        // Remove duplicados antigos e adiciona novos
                        listaAtual.removeAll { antigo -> listaParcial.any { novo -> novo.id == antigo.id } }
                        listaAtual.addAll(listaParcial)

                        // Ordena por data (mais recente primeiro)
                        val listaOrdenada = listaAtual.sortedByDescending { it.dataCriacao?.seconds ?: 0L }

                        atualizarEstado(listaOrdenada)
                    }
            }
        }
    }

    private suspend fun atualizarEstado(pedidos: List<Pedido>) {
        val nomes = carregarNomesPedidos(pedidos)
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            pedidos = pedidos,
            beneficiarios = nomes
        )
    }

    private suspend fun carregarNomesPedidos(pedidos: List<Pedido>): Map<String, String> {
        val nomesMap = _uiState.value.beneficiarios.toMutableMap()

        pedidos.forEach { pedido ->
            if (!nomesMap.containsKey(pedido.beneficiarioId)) {
                val result = beneficiarioRepository.obterBeneficiario(pedido.beneficiarioId)

                nomesMap[pedido.beneficiarioId] = when (result) {
                    is ResultWrapper.Success -> result.value.nome ?: "Sem Nome"
                    else -> "ID: ${pedido.beneficiarioId}"
                }
            }
        }
        return nomesMap
    }
}