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
import com.example.lojasocial.repositories.TrackedPedidoRepository

enum class PedidosTab {
    NOVOS,
    HISTORICO
}

data class PedidosState(
    val isLoading: Boolean = true,
    val pedidos: List<Pedido> = emptyList(),
    val beneficiarios: Map<String, String> = emptyMap(),
    val error: String? = null,
    val currentTab: PedidosTab = PedidosTab.NOVOS,
    val showOnlyTracked: Boolean = false,
    val trackedIds: Set<String> = emptySet()
)

@HiltViewModel
class PedidosListViewModel @Inject constructor(
    private val pedidoRepository: PedidoRepository,
    private val beneficiarioRepository: BeneficiarioRepository,
    private val trackedPedidoRepository: TrackedPedidoRepository
) : ViewModel() {

    private val _uiState = mutableStateOf(PedidosState())
    val uiState: State<PedidosState> = _uiState
    private var pedidosRaw: List<Pedido> = emptyList()

    private var currentJob: Job? = null

    fun alternarFiltroTracked() {
        _uiState.value =
            _uiState.value.copy(showOnlyTracked = !_uiState.value.showOnlyTracked)
        aplicarFiltro()
    }

    fun toggleTracked(pedidoId: String) {
        viewModelScope.launch {
            trackedPedidoRepository.toggle(pedidoId)
        }
    }

    private fun aplicarFiltro() {
        val state = _uiState.value

        val filtrados = if (state.showOnlyTracked) {
            pedidosRaw.filter { it.id in state.trackedIds }
        } else {
            pedidosRaw
        }

        _uiState.value = state.copy(
            pedidos = filtrados,
            isLoading = false
        )
    }

    private fun observarTracked() {
        viewModelScope.launch {
            trackedPedidoRepository.observeTrackedIds().collect { ids ->
                _uiState.value = _uiState.value.copy(trackedIds = ids)
                aplicarFiltro()
            }
        }
    }

    init {
        mudarTab(PedidosTab.NOVOS)
        observarTracked()
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
                pedidoRepository.getPedidosPorStatus(PedidoStatus.NOVO)
                    .collect { pedidos ->
                        atualizarEstado(pedidos)
                    }
            } else {

                val flowAceites = pedidoRepository.getPedidosPorStatus(PedidoStatus.EM_ANDAMENTO)

                val flowRecusados = pedidoRepository.getPedidosPorStatus(PedidoStatus.RECUSADO)

                merge(flowAceites, flowRecusados)
                    .collect { listaParcial ->

                        val listaAtual = _uiState.value.pedidos.toMutableList()

                        listaAtual.removeAll { antigo -> listaParcial.any { novo -> novo.id == antigo.id } }
                        listaAtual.addAll(listaParcial)

                        val listaOrdenada = listaAtual.sortedByDescending { it.dataCriacao?.seconds ?: 0L }

                        atualizarEstado(listaOrdenada)
                    }
            }
        }
    }

    private suspend fun atualizarEstado(pedidos: List<Pedido>) {
        pedidosRaw = pedidos

        val nomes = carregarNomesPedidos(pedidosRaw)

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            beneficiarios = nomes
        )

        aplicarFiltro()
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