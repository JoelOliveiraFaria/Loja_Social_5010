package com.example.lojasocial.ui.entrega

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.*
import com.example.lojasocial.repositories.BeneficiarioRepository
import com.example.lojasocial.repositories.EntregaRepository
import com.example.lojasocial.repositories.PedidoRepository
import com.example.lojasocial.repositories.ProdutoRepository
import com.example.lojasocial.repositories.ResultWrapper
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
        private set

    var nomeBeneficiario = mutableStateOf("")
        private set

    var textoPedido = mutableStateOf("")
        private set

    var erro = mutableStateOf<String?>(null)
        private set

    private val _produtosDisponiveis = MutableStateFlow<List<Produto>>(emptyList())
    val produtosDisponiveis: StateFlow<List<Produto>> = _produtosDisponiveis

    // ---------------- Carregar Entrega ----------------
    fun carregarEntrega(entregaId: String) {
        viewModelScope.launch {
            try {
                val e = entregaRepository.getEntregaById(entregaId) ?: return@launch
                entrega.value = e

                e.beneficiarioId?.let {
                    when (val r = beneficiarioRepository.obterBeneficiario(it)) {
                        is ResultWrapper.Success -> nomeBeneficiario.value = r.value.nome ?: ""
                        else -> {}
                    }
                }

                e.pedidoId?.let {
                    pedidoRepository.getPedidoById(it)?.let { pedido ->
                        textoPedido.value = pedido.textoPedido
                    }
                }

                carregarProdutosInventario()

            } catch (ex: Exception) {
                erro.value = ex.message
            }
        }
    }

    // ---------------- Inventário REAL (por lotes) ----------------
    private fun carregarProdutosInventario() {
        viewModelScope.launch {
            produtoRepository.getProdutos().collectLatest { result ->
                if (result is ResultWrapper.Success) {

                    val produtosComStock = mutableListOf<Produto>()

                    result.value.forEach { produto ->
                        val produtoId = produto.id ?: return@forEach

                        produtoRepository.observeLotes(produtoId)
                            .collectLatest { loteResult ->
                                if (loteResult is ResultWrapper.Success) {
                                    val stock = loteResult.value.sumOf { it.quantidade }

                                    if (stock > 0) {
                                        produtosComStock.add(
                                            produto.copy(quantidadeTotal = stock)
                                        )
                                    }
                                }
                            }
                    }

                    _produtosDisponiveis.value = produtosComStock
                }
            }
        }
    }

    // ---------------- Produtos da Entrega ----------------
    fun adicionarProduto(produto: Produto, quantidade: Int) {
        val atual = entrega.value ?: return

        val itens = atual.itens.toMutableList()
        val index = itens.indexOfFirst { it.produtoId == produto.id }

        if (index >= 0) {
            val item = itens[index]
            val qtdAtual = item.lotesConsumidos.sumOf { it.quantidade }

            itens[index] = item.copy(
                lotesConsumidos = listOf(
                    LoteConsumido("manual", qtdAtual + quantidade)
                )
            )
        } else {
            itens.add(
                ItemEntrega(
                    produtoId = produto.id ?: "",
                    produtoNome = produto.nome,
                    lotesConsumidos = listOf(
                        LoteConsumido("manual", quantidade)
                    )
                )
            )
        }

        entrega.value = atual.copy(itens = itens)
    }

    private suspend fun consumirLotesProduto(
        produtoId: String,
        quantidadeNecessaria: Int
    ) {
        var quantidadeRestante = quantidadeNecessaria

        val resultado = produtoRepository.observeLotes(produtoId)
            .first { it !is ResultWrapper.Loading }

        if (resultado !is ResultWrapper.Success) {
            throw Exception("Erro ao carregar lotes")
        }

        val lotesOrdenados = resultado.value
            .sortedWith(
                compareBy<LoteStock> {
                    it.dataValidade == null // null vai para o fim
                }.thenBy {
                    it.dataValidade
                }
            )

        val stockTotal = lotesOrdenados.sumOf { it.quantidade }
        if (stockTotal < quantidadeNecessaria) {
            throw Exception("Stock insuficiente para concluir a entrega")
        }

        for (lote in lotesOrdenados) {
            if (quantidadeRestante <= 0) break

            val consumir = minOf(lote.quantidade, quantidadeRestante)
            val novaQuantidade = lote.quantidade - consumir

            if (novaQuantidade == 0) {
                produtoRepository.eliminarLote(produtoId, lote.id!!)
            } else {
                produtoRepository.atualizarQuantidadeLote(
                    produtoId,
                    lote.id!!,
                    novaQuantidade
                )
            }

            quantidadeRestante -= consumir
        }
    }

    fun aumentarQuantidade(produtoId: String) {
        alterarQuantidade(produtoId, +1)
    }

    fun diminuirQuantidade(produtoId: String) {
        alterarQuantidade(produtoId, -1)
    }

    private fun alterarQuantidade(produtoId: String, delta: Int) {
        val atual = entrega.value ?: return

        val novosItens = atual.itens.mapNotNull { item ->
            if (item.produtoId == produtoId) {
                val novaQtd = item.lotesConsumidos.sumOf { it.quantidade } + delta
                if (novaQtd <= 0) null
                else item.copy(
                    lotesConsumidos = listOf(
                        LoteConsumido("manual", novaQtd)
                    )
                )
            } else item
        }

        entrega.value = atual.copy(itens = novosItens)
    }

    fun removerProduto(produtoId: String) {
        val atual = entrega.value ?: return
        entrega.value = atual.copy(itens = atual.itens.filterNot { it.produtoId == produtoId })
    }

    // ---------------- Guardar ----------------
    fun salvarAlteracoes(onSuccess: () -> Unit) {
        viewModelScope.launch {
            entrega.value?.let {
                entregaRepository.atualizarEntrega(it)
                onSuccess()
            }
        }
    }

    // ---------------- Terminar Entrega + Pedido ----------------
    fun marcarComoTerminada(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val e = entrega.value ?: return@launch


                e.itens.forEach { item ->
                    val quantidade = item.lotesConsumidos.sumOf { it.quantidade }
                    consumirLotesProduto(item.produtoId, quantidade)
                }


                e.pedidoId?.let { pedidoId ->
                    pedidoRepository.atualizarStatus(
                        pedidoId,
                        PedidoStatus.TERMINADO
                    )
                }


                entregaRepository.atualizarEntrega(
                    e.copy(status = EntregaStatus.TERMINADO)
                )

                onSuccess()

            } catch (ex: Exception) {
                erro.value = ex.message
            }
        }
    }
}

