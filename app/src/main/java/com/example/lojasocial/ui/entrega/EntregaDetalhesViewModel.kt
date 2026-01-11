package com.example.lojasocial.ui.entrega

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.*
import com.example.lojasocial.repositories.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntregaDetalhesViewModel @Inject constructor(
    private val entregaRepository: EntregaRepository,
    private val beneficiarioRepository: BeneficiarioRepository,
    private val pedidoRepository: PedidoRepository,
    private val produtoRepository: ProdutoRepository
) : ViewModel() {

    var entrega = mutableStateOf<Entrega?>(null)
    var nomeBeneficiario = mutableStateOf("")
    var textoPedido = mutableStateOf("")
    var erro = mutableStateOf<String?>(null)
    var avisoStock = mutableStateOf<String?>(null)

    private val _produtosDisponiveis = MutableStateFlow<List<Produto>>(emptyList())
    val produtosDisponiveis: StateFlow<List<Produto>> = _produtosDisponiveis

    fun carregarEntrega(entregaId: String) {
        viewModelScope.launch {
            try {
                val e = entregaRepository.getEntregaById(entregaId) ?: return@launch
                entrega.value = e

                e.beneficiarioId?.let {
                    val r = beneficiarioRepository.obterBeneficiario(it)
                    if (r is ResultWrapper.Success) nomeBeneficiario.value = r.value.nome ?: ""
                }

                e.pedidoId?.let {
                    pedidoRepository.getPedidoById(it)?.let { pedido ->
                        textoPedido.value = pedido.textoPedido
                    }
                }
                carregarProdutosInventario()
            } catch (e: Exception) {
                erro.value = e.message
            }
        }
    }

    private fun carregarProdutosInventario() {
        viewModelScope.launch {
            produtoRepository.getProdutos().collectLatest { result ->
                if (result is ResultWrapper.Success) {
                    _produtosDisponiveis.value = result.value
                }
            }
        }
    }

    fun atualizarData(novaData: Long?) {
        entrega.value = entrega.value?.copy(dataEntrega = novaData)
    }

    fun atualizarStatus(novoStatus: EntregaStatus) {
        entrega.value = entrega.value?.copy(status = novoStatus)
    }

    fun adicionarProduto(produto: Produto) {
        val atual = entrega.value ?: return
        val itens = atual.itens.toMutableList()
        val existente = itens.find { it.produtoId == produto.id }

        if (existente != null) {
            aumentarQuantidade(produto.id!!)
        } else {
            if (produto.quantidadeTotal > 0) {
                itens.add(ItemEntrega(
                    produtoId = produto.id!!,
                    produtoNome = produto.nome,
                    lotesConsumidos = listOf(LoteConsumido("manual", 1))
                ))
                entrega.value = atual.copy(itens = itens)
            } else {
                avisoStock.value = "Produto sem stock disponível."
            }
        }
    }

    fun aumentarQuantidade(produtoId: String) {
        val atual = entrega.value ?: return
        val stockMax = _produtosDisponiveis.value.find { it.id == produtoId }?.quantidadeTotal ?: 0

        val novosItens = atual.itens.map { item ->
            if (item.produtoId == produtoId) {
                val qtdAtual = item.lotesConsumidos.sumOf { it.quantidade }
                if (qtdAtual >= stockMax) {
                    avisoStock.value = "Limite de stock atingido."
                    item
                } else {
                    val lote = item.lotesConsumidos.first()
                    item.copy(lotesConsumidos = listOf(lote.copy(quantidade = lote.quantidade + 1)))
                }
            } else item
        }
        entrega.value = atual.copy(itens = novosItens)
    }

    fun diminuirQuantidade(produtoId: String) {
        val atual = entrega.value ?: return
        val novosItens = atual.itens.map { item ->
            if (item.produtoId == produtoId) {
                val lote = item.lotesConsumidos.first()
                if (lote.quantidade > 1) {
                    item.copy(lotesConsumidos = listOf(lote.copy(quantidade = lote.quantidade - 1)))
                } else item
            } else item
        }
        entrega.value = atual.copy(itens = novosItens)
    }

    fun removerProduto(produtoId: String) {
        val atual = entrega.value ?: return
        entrega.value = atual.copy(itens = atual.itens.filterNot { it.produtoId == produtoId })
    }

    fun guardar(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                entrega.value?.let { entregaRepository.atualizarEntrega(it) }
                onSuccess()
            } catch (e: Exception) { erro.value = e.message }
        }
    }

    fun limparAviso() { avisoStock.value = null }
}