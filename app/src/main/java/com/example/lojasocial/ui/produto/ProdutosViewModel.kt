package com.example.lojasocial.ui.produtos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.LoteStock
import com.example.lojasocial.models.Produto
import com.example.lojasocial.repositories.ProdutoRepository
import com.example.lojasocial.repositories.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class InventarioUIState(
    val isLoading: Boolean = false,
    val itens: List<Produto> = emptyList(),
    val error: String? = null,
    val lotesPorProduto: Map<String, List<LoteStock>> = emptyMap()
)

@HiltViewModel
class ProdutosViewModel @Inject constructor(
    private val repository: ProdutoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventarioUIState())
    val uiState: StateFlow<InventarioUIState> = _uiState.asStateFlow()

    init { carregarInventario() }

    // Correção para API 24: Gera data AAAA-MM-DD sem usar LocalDate
    fun getHojeStr(): String {
        val c = Calendar.getInstance()
        return String.format("%04d-%02d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
    }

    fun carregarInventario() {
        viewModelScope.launch {
            repository.getProdutos().collect { result ->
                if (result is ResultWrapper.Success) {
                    _uiState.value = _uiState.value.copy(itens = result.value)
                    result.value.forEach { p -> p.id?.let { carregarLotes(it) } }
                }
            }
        }
    }

    private fun carregarLotes(produtoId: String) {
        viewModelScope.launch {
            repository.observeLotes(produtoId).collect { res ->
                if (res is ResultWrapper.Success) {
                    val hoje = getHojeStr()
                    val novosLotes = _uiState.value.lotesPorProduto.toMutableMap()
                    novosLotes[produtoId] = res.value

                    // Soma apenas o que não expirou ou não tem validade
                    val totalValido = res.value.filter { it.dataValidade == null || it.dataValidade!! >= hoje }.sumOf { it.quantidade }

                    val novosItens = _uiState.value.itens.map {
                        if (it.id == produtoId) it.copy(quantidadeTotal = totalValido) else it
                    }

                    _uiState.value = _uiState.value.copy(lotesPorProduto = novosLotes, itens = novosItens)
                }
            }
        }
    }

    fun adicionarLote(produtoId: String, lote: LoteStock) {
        viewModelScope.launch {
            repository.adicionarLote(produtoId, lote)
            carregarLotes(produtoId)
        }
    }

    fun apagarLotesExpirados(produtoId: String) {
        viewModelScope.launch {
            val hoje = getHojeStr()
            val lotes = _uiState.value.lotesPorProduto[produtoId] ?: return@launch
            lotes.filter { it.dataValidade != null && it.dataValidade!! < hoje }.forEach { lote ->
                lote.id?.let { repository.eliminarLote(produtoId, it) }
            }
            carregarLotes(produtoId)
        }
    }

    fun criarProdutoMestre(nome: String, descricao: String) {
        viewModelScope.launch {
            repository.adicionarProduto(Produto(nome = nome, descricao = descricao))
            carregarInventario()
        }
    }

    fun eliminarLote(produtoId: String, loteId: String) {
        viewModelScope.launch {
            val result = repository.eliminarLote(produtoId, loteId)
            if (result is ResultWrapper.Success) {
                carregarLotes(produtoId)
                carregarInventario()
            } else if (result is ResultWrapper.Error) {
                println("Erro ao eliminar: ${result.message}")
            }
        }
    }
}