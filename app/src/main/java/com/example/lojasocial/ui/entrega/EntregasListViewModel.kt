package com.example.lojasocial.ui.entregas

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.EntregaStatus
import com.example.lojasocial.repositories.BeneficiarioRepository
import com.example.lojasocial.repositories.EntregaRepository
import com.example.lojasocial.repositories.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest

@HiltViewModel
class EntregasListViewModel @Inject constructor(
    private val entregaRepository: EntregaRepository,
    private val beneficiarioRepository: BeneficiarioRepository
) : ViewModel() {

    private val _filtroStatus = MutableStateFlow<EntregaStatus?>(EntregaStatus.EM_ANDAMENTO)
    val filtroStatus: StateFlow<EntregaStatus?> = _filtroStatus


    val entregas = _filtroStatus.flatMapLatest { status ->
        if (status == null) {
            entregaRepository.getEntregasPorStatus(EntregaStatus.EM_ANDAMENTO)
        } else {
            entregaRepository.getEntregasPorStatus(status)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun alterarFiltro(novoStatus: EntregaStatus?) {
        _filtroStatus.value = novoStatus
    }
    private val _nomesBeneficiarios = mutableStateMapOf<String, String>()
    val nomesBeneficiarios: Map<String, String> = _nomesBeneficiarios

    fun carregarNomeBeneficiario(id: String) {
        if (_nomesBeneficiarios.containsKey(id)) return

        viewModelScope.launch {
            when (val result = beneficiarioRepository.obterBeneficiario(id)) {
                is ResultWrapper.Success -> {
                    _nomesBeneficiarios[id] = result.value.nome.toString()
                }
                else -> {
                    _nomesBeneficiarios[id] = "Beneficiário desconhecido"
                }
            }
        }
    }
}


