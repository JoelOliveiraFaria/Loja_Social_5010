package com.example.lojasocial.ui.entrega

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

@HiltViewModel
class EntregasTerminadasListViewModel @Inject constructor(
    private val entregaRepository: EntregaRepository,
    private val beneficiarioRepository: BeneficiarioRepository
) : ViewModel() {

    val entregas = entregaRepository
        .getEntregasPorStatus(EntregaStatus.PRONTO)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

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
