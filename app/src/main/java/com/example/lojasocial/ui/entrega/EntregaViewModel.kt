package com.example.lojasocial.ui.entrega

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.Entrega
import com.example.lojasocial.models.ProdutoEntrega
import com.example.lojasocial.repositories.EntregaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntregaViewModel @Inject constructor(
    private val entregaRepository: EntregaRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<Entrega?>(null)
    val uiState: State<Entrega?> = _uiState

    fun iniciarEntrega(
        beneficiarioId: String,
        pedidoId: String
    ) {
        _uiState.value = Entrega(
            beneficiarioId = beneficiarioId,
            pedidoId = pedidoId
        )
    }

    fun adicionarProduto(produtoId: String, quantidade: Int) {
        val entregaAtual = _uiState.value ?: return

        _uiState.value = entregaAtual.copy(
            produtos = entregaAtual.produtos + ProdutoEntrega(produtoId, quantidade)
        )
    }

    fun removerProduto(produtoId: String) {
        val entregaAtual = _uiState.value ?: return

        _uiState.value = entregaAtual.copy(
            produtos = entregaAtual.produtos.filterNot {
                it.produtoId == produtoId
            }
        )
    }

    fun salvarEntrega(
        onSuccess: (entregaId: String) -> Unit,
        onError: (String) -> Unit
    ) {
        val entrega = _uiState.value
        if (entrega == null) {
            onError("Entrega não iniciada")
            return
        }

        viewModelScope.launch {
            try {
                val entregaId = entregaRepository.criarEntrega(entrega)
                onSuccess(entregaId)
            } catch (e: Exception) {
                onError(e.message ?: "Erro ao criar entrega")
            }
        }
    }
}
