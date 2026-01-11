package com.example.lojasocial.ui.entregas

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.Beneficiario
import com.example.lojasocial.models.EntregaStatus
import com.example.lojasocial.repositories.BeneficiarioRepository
import com.example.lojasocial.repositories.EntregaRepository
import com.example.lojasocial.repositories.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntregasListViewModel @Inject constructor(
    private val entregaRepository: EntregaRepository,
    private val beneficiarioRepository: BeneficiarioRepository
) : ViewModel() {

    private val _filtroStatus = MutableStateFlow<EntregaStatus?>(EntregaStatus.EM_ANDAMENTO)
    val filtroStatus: StateFlow<EntregaStatus?> = _filtroStatus

    val entregas = _filtroStatus.flatMapLatest { status ->
        entregaRepository.getEntregasPorStatus(status ?: EntregaStatus.EM_ANDAMENTO)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    private val _dadosBeneficiarios = mutableStateMapOf<String, Beneficiario>()
    val dadosBeneficiarios: Map<String, Beneficiario> = _dadosBeneficiarios

    fun carregarDadosBeneficiario(id: String) {
        if (id.isEmpty() || _dadosBeneficiarios.containsKey(id)) return
        viewModelScope.launch {
            when (val result = beneficiarioRepository.obterBeneficiario(id)) {
                is ResultWrapper.Success -> _dadosBeneficiarios[id] = result.value
                else -> {}
            }
        }
    }

    fun alterarFiltro(novoStatus: EntregaStatus?) {
        _filtroStatus.value = novoStatus
    }
}